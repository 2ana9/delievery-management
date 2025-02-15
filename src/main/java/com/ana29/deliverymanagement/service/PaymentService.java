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

	@Transactional
	public PaymentResultDto createPayment(CreatePaymentRequestDto requestDto, String userId) {

		//주문 정보 가져오기
		Order order = orderRepository.findById(requestDto.orderId())
			.orElseThrow(() -> new RuntimeException("Order not found"));

		//유저의 주문인지 확인
		if(!order.getUser().getId().equals(userId)){
			throw new RuntimeException("Order does not belong to user");
		}

		//결제 수단
		PaymentTypeEnum paymentType = PaymentTypeEnum.valueOf(requestDto.paymentType());

		//결제 요청 DTO 생성
		PaymentRequestDto paymentRequestDto =
			new PaymentRequestDto(order.getTotalPrice(), paymentType);

		//결제 처리
		PaymentResultDto resultDto = paymentProcessor.processPayment(paymentRequestDto);

		// 결제 성공 여부 확인
		if (resultDto.isSuccess()) {
			order.updateStatus(OrderStatusEnum.PAID);
		}

		// 결제 정보 저장
		paymentRepository.save(
			Payment.builder()
				.totalPrice(order.getTotalPrice())
				.order(order)
				.paymentType(paymentType)
				.paymentStatus(resultDto.isSuccess() ? PaymentStatusEnum.COMPLETED : PaymentStatusEnum.FAILED)
				.externalPaymentId(resultDto.externalPaymentId())
				.errorMessage(resultDto.errorMessage())
				.build()
		);

		return resultDto;
	}
}
