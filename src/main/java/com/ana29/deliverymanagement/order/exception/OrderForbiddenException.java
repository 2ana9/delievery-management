package com.ana29.deliverymanagement.order.exception;

import java.util.UUID;

public class OrderForbiddenException extends RuntimeException {

	public OrderForbiddenException(UUID orderId) {
		super("접근 권한이 없습니다. ID: " + orderId);
	}
}
