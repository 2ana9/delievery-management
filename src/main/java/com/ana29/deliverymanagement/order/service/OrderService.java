package com.ana29.deliverymanagement.order.service;

import com.ana29.deliverymanagement.global.constant.OrderTypeEnum;
import com.ana29.deliverymanagement.global.constant.PaymentTypeEnum;
import com.ana29.deliverymanagement.order.dto.*;
import com.ana29.deliverymanagement.order.entity.Order;
import com.ana29.deliverymanagement.order.entity.Payment;
import com.ana29.deliverymanagement.order.exception.MissingUserAddressException;
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
import com.ana29.deliverymanagement.user.constant.UserRoleEnum;
import com.ana29.deliverymanagement.user.entity.UserAddress;
import com.ana29.deliverymanagement.user.exception.UserAddressNotFoundException;
import com.ana29.deliverymanagement.user.repository.UserAddressRepository;
import com.ana29.deliverymanagement.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final MenuRepository menuRepository;
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final PaymentRepository paymentRepository;
	private final PaymentProcessor paymentProcessor;
	private final RestaurantRepository restaurantRepository;
	private final UserAddressRepository userAddressRepository;

	@Transactional
	public OrderDetailResponseDto createOrder(CreateOrderRequestDto requestDto, String userId) {

		UserAddress userAddress = getUserAddress(requestDto, userId);
		Menu menu = getMenuById(requestDto.menuId());
		Order order = createOrder(requestDto, userId, menu, userAddress);
		Payment payment = createPayment(order, requestDto.paymentType());

		return OrderDetailResponseDto.from(order, payment);
	}

	private UserAddress getUserAddress(CreateOrderRequestDto requestDto, String userId) {
		if (requestDto.orderType() == OrderTypeEnum.ONLINE && requestDto.userAddressId() == null) {
			throw new MissingUserAddressException();
		}

		if (requestDto.orderType() == OrderTypeEnum.ONLINE) {
			return userAddressRepository.findByIdAndUserIdAndIsDeletedFalse(
					requestDto.userAddressId(), userId)
				.orElseThrow(UserAddressNotFoundException::new);
		}
		return null;
	}


	private Menu getMenuById(UUID menuId) {
		return menuRepository.findMenuWithRestaurant(menuId)
			.orElseThrow(() -> new MenuNotFoundException(menuId));
	}

	private Order createOrder(CreateOrderRequestDto requestDto, String userId, Menu menu,
		UserAddress userAddress) {
		return orderRepository
			.save(Order.of(userRepository.getReferenceById(userId), menu, requestDto,
				userAddress));
	}

	private Payment createPayment(Order order, PaymentTypeEnum paymentType) {
		return paymentRepository.save(Payment.from(order, processPayment(order, paymentType)));
	}

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

	@Transactional(readOnly = true)
	public Page<OrderHistoryResponseDto> getOrderHistory(OrderSearchCondition condition,
		Pageable pageable, String userId, List<String> foodTypes) {
		return orderRepository.findOrderHistory(userId, condition, pageable, foodTypes);
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

	private boolean isAdmin(UserDetailsImpl userDetails) {
		UserRoleEnum role = userDetails.getRole();
		return role.equals(UserRoleEnum.MANAGER) || role.equals(UserRoleEnum.MASTER);
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

	@Transactional
	public void deleteOrder(UUID id, String userId) {
		orderRepository.findWithPaymentById(id)
			.ifPresent(order -> order.delete(userId));
	}
}
