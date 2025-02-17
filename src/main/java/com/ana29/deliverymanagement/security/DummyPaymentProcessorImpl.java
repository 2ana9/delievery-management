package com.ana29.deliverymanagement.security;

import com.ana29.deliverymanagement.order.dto.PaymentRequestDto;
import com.ana29.deliverymanagement.order.dto.PaymentResultDto;
import com.ana29.deliverymanagement.order.service.PaymentProcessor;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DummyPaymentProcessorImpl implements PaymentProcessor {

	@Override
	public PaymentResultDto processPayment(PaymentRequestDto requestDto) {
		return PaymentResultDto.success(UUID.randomUUID(), requestDto.paymentType());
	}
}
