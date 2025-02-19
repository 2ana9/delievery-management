package com.ana29.deliverymanagement.order.service;

import com.ana29.deliverymanagement.global.constant.PaymentTypeEnum;
import com.ana29.deliverymanagement.order.dto.CreateOrderRequestDto;
import com.ana29.deliverymanagement.order.dto.OrderDetailResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderHistoryResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderSearchCondition;
import com.ana29.deliverymanagement.order.dto.PaymentRequestDto;
import com.ana29.deliverymanagement.order.dto.PaymentResultDto;
import com.ana29.deliverymanagement.order.dto.RefundRequestDto;
import com.ana29.deliverymanagement.order.entity.Order;
import com.ana29.deliverymanagement.order.entity.Payment;
import com.ana29.deliverymanagement.order.exception.OrderAccessDeniedException;
import com.ana29.deliverymanagement.order.exception.OrderNotFoundException;
import com.ana29.deliverymanagement.order.exception.PaymentFailException;
import com.ana29.deliverymanagement.order.repository.OrderRepository;
import com.ana29.deliverymanagement.order.repository.PaymentRepository;
import com.ana29.deliverymanagement.restaurant.entity.Menu;
import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import com.ana29.deliverymanagement.restaurant.exception.MenuNotFoundException;
import com.ana29.deliverymanagement.restaurant.exception.RestaurantAccessDeniedException;
import com.ana29.deliverymanagement.restaurant.exception.RestaurantNotFoundException;
import com.ana29.deliverymanagement.restaurant.repository.MenuRepository;
import com.ana29.deliverymanagement.restaurant.repository.RestaurantRepository;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final MenuRepository menuRepository;
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final PaymentRepository paymentRepository;
	private final PaymentProcessor paymentProcessor;
	private final RestaurantRepository restaurantRepository;

	@Transactional
	public OrderDetailResponseDto createOrder(CreateOrderRequestDto requestDto, String userId) {
		//메뉴와 레스토랑 정보 한 번에 조회 (식당 정보 전달 위함)
		Menu menu = menuRepository.findMenuWithRestaurant(requestDto.menuId())
			.orElseThrow(() -> new MenuNotFoundException(requestDto.menuId()));

		//새로운 주문 생성
		Order order = orderRepository
			.save(Order.from(userRepository.getReferenceById(userId), menu, requestDto));

		Payment payment = createPayment(order, requestDto.paymentType());
		return OrderDetailResponseDto.from(order, payment);
	}

	@Transactional(readOnly = true)
	public Page<OrderHistoryResponseDto> getOrderHistory(OrderSearchCondition condition,
		Pageable pageable, String userId) {
		return orderRepository.findOrderHistory(userId, condition, pageable);
	}

	@Transactional(readOnly = true)
	public Page<OrderHistoryResponseDto> getRestaurantOrderHistory(OrderSearchCondition condition,
		Pageable pageable, UserDetailsImpl userDetails, UUID restaurantId) {

		Restaurant restaurant = restaurantRepository.findById(restaurantId)
			.orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

		if (!isAdmin(userDetails) && !restaurant.isOwner(userDetails.getUsername())) {
			throw new RestaurantAccessDeniedException(restaurantId);
		}

		return orderRepository.findRestaurantOrderHistory(restaurantId, condition, pageable);
	}

	@Transactional(readOnly = true)
	public OrderDetailResponseDto getOrderDetail(UUID orderId, String userId) {
		Order order = findOrder(orderId, userId);
		validateOrderAccess(userId, order);
		return OrderDetailResponseDto.from(order, order.getPayment());
	}

	private Order findOrder(UUID orderId, String userId) {
		return orderRepository.findOrderById(orderId, userId)
			.orElseThrow(() -> new OrderNotFoundException(orderId));
	}

	private void validateOrderAccess(String userId, Order order) {
		if (!order.isOwner(userId) && !order.getMenu().getRestaurant().isOwner(userId)) {
			throw new OrderAccessDeniedException(order.getId());
		}
	}

	private Payment createPayment(Order order, PaymentTypeEnum paymentType) {
		return paymentRepository.save(Payment.from(order, processPayment(order, paymentType)));
	}

	//결제처리
	private PaymentResultDto processPayment(Order order, PaymentTypeEnum paymentType) {
		PaymentResultDto resultDto =
			paymentProcessor.processPayment(
				new PaymentRequestDto(order.getTotalPrice(), paymentType));

		if (!resultDto.isSuccess()) {
			throw new PaymentFailException(resultDto);
		}

		order.pay();
		return resultDto;
	}

	@Transactional
	public OrderDetailResponseDto cancelOrder(UUID orderId, String userId) {
		Order order = findOrder(orderId, userId);
		validateOrderAccess(userId, order);
		refundPayment(order);
		return OrderDetailResponseDto.from(order, order.getPayment());
	}

	//환불처리
	private void refundPayment(Order order) {
		PaymentResultDto resultDto =
			paymentProcessor.refundPayment(
				new RefundRequestDto(order.getPayment().getExternalPaymentId(),
					order.getTotalPrice(), "Default reason"));

		if (!resultDto.isSuccess()) {
			throw new PaymentFailException(resultDto);
		}

		order.getPayment().refund();
		order.cancel();
	}

	private boolean isAdmin(UserDetailsImpl userDetails) {
		return userDetails.getAuthorities().stream()
			.anyMatch(a ->
				a.getAuthority().equals("ROLE_MANAGER") || a.getAuthority().equals("ROLE_MASTER"));
	}

}
