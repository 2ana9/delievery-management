package com.ana29.deliverymanagement.order.service;

import com.ana29.deliverymanagement.order.dto.PaymentRequestDto;
import com.ana29.deliverymanagement.order.dto.PaymentResultDto;
import com.ana29.deliverymanagement.order.dto.RefundRequestDto;

public interface PaymentProcessor {

	PaymentResultDto processPayment(PaymentRequestDto requestDto);

	PaymentResultDto refundPayment(RefundRequestDto requestDto);
}
