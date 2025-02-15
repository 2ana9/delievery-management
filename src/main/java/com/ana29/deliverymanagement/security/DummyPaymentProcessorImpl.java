package com.ana29.deliverymanagement.security;

import com.ana29.deliverymanagement.dto.PaymentRequestDto;
import com.ana29.deliverymanagement.dto.PaymentResultDto;
import com.ana29.deliverymanagement.service.PaymentProcessor;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DummyPaymentProcessorImpl implements PaymentProcessor {

	@Override
	public PaymentResultDto processPayment(PaymentRequestDto requestDto) {
		return PaymentResultDto.success(UUID.randomUUID());
	}
}
