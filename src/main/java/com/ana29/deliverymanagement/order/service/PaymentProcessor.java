package com.ana29.deliverymanagement.order.service;

import com.ana29.deliverymanagement.order.dto.PaymentRequestDto;
import com.ana29.deliverymanagement.order.dto.PaymentResultDto;

public interface PaymentProcessor {

	PaymentResultDto processPayment(PaymentRequestDto requestDto);
}
