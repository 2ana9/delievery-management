package com.ana29.deliverymanagement.order.exception;

import java.util.UUID;


public class OrderNotFoundException extends RuntimeException {

	public OrderNotFoundException(UUID orderId) {
		super("주문 ID를 찾을 수 없습니다. ID: " + orderId);
	}
}


