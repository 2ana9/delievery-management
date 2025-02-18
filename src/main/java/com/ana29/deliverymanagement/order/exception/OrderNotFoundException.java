package com.ana29.deliverymanagement.order.exception;

import com.ana29.deliverymanagement.global.exception.CustomNotFoundException;
import java.util.UUID;


public class OrderNotFoundException extends CustomNotFoundException {

	public OrderNotFoundException(UUID orderId) {
		super("주문 ID를 찾을 수 없습니다. ID: " + orderId);
	}
}


