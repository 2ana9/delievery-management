package com.ana29.deliverymanagement.order.dto;

import com.ana29.deliverymanagement.global.constant.PaymentTypeEnum;

public record PaymentRequestDto(
	Long totalPrice,
	PaymentTypeEnum paymentType) {
}
