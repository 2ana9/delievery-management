package com.ana29.deliverymanagement.service;

import com.ana29.deliverymanagement.constant.OrderStatusEnum;
import com.ana29.deliverymanagement.constant.PaymentStatusEnum;
import com.ana29.deliverymanagement.constant.PaymentTypeEnum;
import com.ana29.deliverymanagement.dto.CreatePaymentRequestDto;
import com.ana29.deliverymanagement.dto.PaymentRequestDto;
import com.ana29.deliverymanagement.dto.PaymentResultDto;
import com.ana29.deliverymanagement.entity.Order;
import com.ana29.deliverymanagement.entity.Payment;
import com.ana29.deliverymanagement.repository.OrderRepository;
import com.ana29.deliverymanagement.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final PaymentProcessor paymentProcessor;
	private final OrderRepository orderRepository;

	//결제 생성 및 처리
	@Transactional
	public PaymentResultDto createPayment(CreatePaymentRequestDto requestDto, String userId) {
		Order order = validateOrder(requestDto, userId);

		PaymentTypeEnum paymentType = PaymentTypeEnum.valueOf(requestDto.paymentType());
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
		}
		return resultDto;
	}

	//주문 정보 검증 : 주문이 존재하는지, 요청한 사용자의 주문인지 확인
	private Order validateOrder(CreatePaymentRequestDto requestDto, String userId) {
		Order order = orderRepository.findById(requestDto.orderId())
			.orElseThrow(() -> new RuntimeException("Order not found"));

		if (!order.getUser().getId().equals(userId)) {
			throw new RuntimeException("Order does not belong to user");
		}
		return order;
	}
}
