package com.ana29.deliverymanagement.service;

import com.ana29.deliverymanagement.constant.OrderStatusEnum;
import com.ana29.deliverymanagement.constant.PaymentTypeEnum;
import com.ana29.deliverymanagement.dto.CreateOrderRequestDto;
import com.ana29.deliverymanagement.dto.CreateOrderResponseDto;
import com.ana29.deliverymanagement.dto.PaymentRequestDto;
import com.ana29.deliverymanagement.dto.PaymentResultDto;
import com.ana29.deliverymanagement.entity.Menu;
import com.ana29.deliverymanagement.entity.Order;
import com.ana29.deliverymanagement.entity.Payment;
import com.ana29.deliverymanagement.entity.User;
import com.ana29.deliverymanagement.exception.PaymentFailException;
import com.ana29.deliverymanagement.repository.MenuRepository;
import com.ana29.deliverymanagement.repository.OrderRepository;
import com.ana29.deliverymanagement.repository.PaymentRepository;
import com.ana29.deliverymanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
	public CreateOrderResponseDto createOrder(CreateOrderRequestDto requestDto, String userId) {
		//메뉴와 레스토랑 정보 한 번에 조회 (식당 정보 전달 위함)
		Menu menu = menuRepository.findMenuWithRestaurant(requestDto.menuId())
			.orElseThrow(() -> new RuntimeException("Menu not found"));

		//새로운 주문 생성 (user는 reference로)
		Order order = orderRepository.save(Order.builder()
			.user(userRepository.getReferenceById(userId))
			.menu(menu)
			.quantity(requestDto.quantity())
			.orderRequest(requestDto.orderRequest())
			.orderStatus(OrderStatusEnum.PENDING)
			.build());

		PaymentResultDto paymentResult = createPayment(order, requestDto.paymentType());

		return CreateOrderResponseDto.of(order, paymentResult);
	}

	private PaymentResultDto createPayment(Order order, PaymentTypeEnum paymentType) {
		PaymentResultDto resultDto = processPayment(order, paymentType);
		paymentRepository.save(Payment.from(order, paymentType, resultDto));
		return resultDto;
	}

	//결제 처리 및 주문상태 업데이트
	private PaymentResultDto processPayment(Order order, PaymentTypeEnum paymentType) {
		PaymentResultDto resultDto = paymentProcessor.processPayment(
			new PaymentRequestDto(order.getTotalPrice(), paymentType));

		if (resultDto.isSuccess()) {
			order.updateStatus(OrderStatusEnum.PAID);
		} else {
			throw new PaymentFailException(resultDto);
		}
		return resultDto;
	}
}
