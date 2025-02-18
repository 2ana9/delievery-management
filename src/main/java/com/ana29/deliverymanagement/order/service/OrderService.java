package com.ana29.deliverymanagement.order.service;

import com.ana29.deliverymanagement.global.constant.OrderStatusEnum;
import com.ana29.deliverymanagement.global.constant.PaymentTypeEnum;
import com.ana29.deliverymanagement.order.dto.CreateOrderRequestDto;
import com.ana29.deliverymanagement.order.dto.OrderHistoryResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderDetailResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderSearchCondition;
import com.ana29.deliverymanagement.order.dto.PaymentRequestDto;
import com.ana29.deliverymanagement.order.dto.PaymentResultDto;
import com.ana29.deliverymanagement.order.entity.Order;
import com.ana29.deliverymanagement.order.entity.Payment;
import com.ana29.deliverymanagement.order.exception.MenuNotFoundException;
import com.ana29.deliverymanagement.order.exception.OrderForbiddenException;
import com.ana29.deliverymanagement.order.exception.OrderNotFoundException;
import com.ana29.deliverymanagement.order.exception.PaymentFailException;
import com.ana29.deliverymanagement.order.repository.OrderRepository;
import com.ana29.deliverymanagement.order.repository.PaymentRepository;
import com.ana29.deliverymanagement.restaurant.entity.Menu;
import com.ana29.deliverymanagement.restaurant.repository.MenuRepository;
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

	@Transactional
	public OrderDetailResponseDto createOrder(CreateOrderRequestDto requestDto, String userId) {
		//메뉴와 레스토랑 정보 한 번에 조회 (식당 정보 전달 위함)
		Menu menu = menuRepository.findMenuWithRestaurant(requestDto.menuId())
			.orElseThrow(() -> new MenuNotFoundException(requestDto.menuId()));

		//새로운 주문 생성
		Order order = orderRepository.save(Order.builder()
			.user(userRepository.getReferenceById(userId))
			.menu(menu)
			.quantity(requestDto.quantity())
			.totalPrice(menu.getPrice() * requestDto.quantity())
			.orderRequest(requestDto.orderRequest())
			.orderStatus(OrderStatusEnum.PENDING)
			.build());

		Payment payment = createPayment(order, requestDto.paymentType());

		return OrderDetailResponseDto.from(order, payment);
	}

	@Transactional(readOnly = true)
	public Page<OrderHistoryResponseDto> getOrderHistory(OrderSearchCondition condition,
		Pageable pageable, String userId) {
		return orderRepository.findOrderHistory(userId, condition, pageable);
	}

	private Payment createPayment(Order order, PaymentTypeEnum paymentType) {
		return paymentRepository.save(Payment.from(order, processPayment(order, paymentType)));
	}

	@Transactional(readOnly = true)
	public OrderDetailResponseDto getOrderDetail(UUID orderId, String userId) {
		Order order = orderRepository.findOrderById(orderId, userId)
			.orElseThrow(() -> new OrderNotFoundException(orderId));

		//관리자, 식당주인 권한 추가
		if(!order.isOwner(userId)) {
			throw new OrderForbiddenException(orderId);
		}
		return OrderDetailResponseDto.from(order, order.getPayment());
	}

	//결제 처리 및 주문상태 업데이트
	private PaymentResultDto processPayment(Order order, PaymentTypeEnum paymentType) {
		PaymentResultDto resultDto =
			paymentProcessor.processPayment(
				new PaymentRequestDto(order.getTotalPrice(), paymentType));

		if (resultDto.isSuccess()) {
			order.updateStatus(OrderStatusEnum.PAID);
		} else {
			throw new PaymentFailException(resultDto);
		}
		return resultDto;
	}
}
