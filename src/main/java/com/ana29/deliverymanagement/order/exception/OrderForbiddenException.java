package com.ana29.deliverymanagement.order.exception;

import com.ana29.deliverymanagement.global.exception.CustomForbiddenException;
import java.util.UUID;

public class OrderForbiddenException extends CustomForbiddenException {

	public OrderForbiddenException(UUID orderId) {
		super("접근 권한이 없습니다. ID: " + orderId);
	}
}
