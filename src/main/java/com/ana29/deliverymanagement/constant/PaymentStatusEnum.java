package com.ana29.deliverymanagement.constant;

import lombok.Getter;

@Getter
public enum PaymentStatusEnum {
	COMPLETED("결제 완료"), // 최종 성공했을때만 COMPLETED
	FAILED("결제 실패");

	private final String description;

	PaymentStatusEnum(String description) {
		this.description = description;
	}

//	public boolean canChangeTo(PaymentStatusEnum nextStatus) {
//		return switch (this) {
//			case PENDING -> nextStatus == COMPLETED || nextStatus == FAILED;
//			case COMPLETED, FAILED -> false;
//		};
//	}
}