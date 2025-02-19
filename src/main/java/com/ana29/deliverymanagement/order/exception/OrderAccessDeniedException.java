package com.ana29.deliverymanagement.order.exception;

import com.ana29.deliverymanagement.global.exception.CustomAccessDeniedException;
import java.util.UUID;

public class OrderAccessDeniedException extends CustomAccessDeniedException {

	public OrderAccessDeniedException(UUID orderId) {
		super("접근 권한이 없습니다. ID: " + orderId);
	}
}
