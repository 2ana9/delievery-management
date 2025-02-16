package com.ana29.deliverymanagement.service;

import com.ana29.deliverymanagement.dto.PaymentRequestDto;
import com.ana29.deliverymanagement.dto.PaymentResultDto;

public interface PaymentProcessor {

	PaymentResultDto processPayment(PaymentRequestDto requestDto);
}
