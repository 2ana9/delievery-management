package com.ana29.deliverymanagement.global.constant;

import lombok.Getter;

@Getter
public enum OrderTypeEnum {
	ONLINE("온라인 주문"),
	OFFLINE("오프라인 주문");

	private final String description;

	OrderTypeEnum(String description) {
		this.description = description;
	}
}