package com.ana29.deliverymanagement.dto;

import java.util.UUID;

public record PaymentResultDto(
	boolean isSuccess,        // 결제 성공 여부
	UUID externalPaymentId,   // 외부 결제 시스템의 결제 ID
	String errorMessage       // 실패시 에러 메시지 (선택)
) {

	public static PaymentResultDto success(UUID externalPaymentId) {
		return new PaymentResultDto(true, externalPaymentId, null);
	}

	public static PaymentResultDto fail(String errorMessage) {
		return new PaymentResultDto(false, null, errorMessage);
	}
}
