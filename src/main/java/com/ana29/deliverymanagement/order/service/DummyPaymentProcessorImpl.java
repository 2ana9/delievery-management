package com.ana29.deliverymanagement.order.service;

import com.ana29.deliverymanagement.global.constant.PaymentStatusEnum;
import com.ana29.deliverymanagement.order.dto.PaymentRequestDto;
import com.ana29.deliverymanagement.order.dto.PaymentResultDto;
import com.ana29.deliverymanagement.order.dto.RefundRequestDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DummyPaymentProcessorImpl implements PaymentProcessor {

	@Override
	public PaymentResultDto processPayment(PaymentRequestDto requestDto) {
		return PaymentResultDto.success(UUID.randomUUID(), requestDto.paymentType());
	}

	@Override
	public PaymentResultDto refundPayment(RefundRequestDto requestDto) {
		return PaymentResultDto.success(requestDto.externalPaymentId(), null);
	}
}
