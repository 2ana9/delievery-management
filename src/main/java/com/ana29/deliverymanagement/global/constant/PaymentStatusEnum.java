package com.ana29.deliverymanagement.global.constant;

import lombok.Getter;

@Getter
public enum PaymentStatusEnum {
	COMPLETED("결제 완료"), // 최종 성공했을때만 COMPLETED
	FAILED("결제 실패"),
	REFUNDED("환불 완료");

	private final String description;

	PaymentStatusEnum(String description) {
		this.description = description;
	}
}