package com.ana29.deliverymanagement.dto;

import com.ana29.deliverymanagement.constant.PaymentTypeEnum;

public record PaymentRequestDto(
	Long amount,
	PaymentTypeEnum paymentType) {
}
