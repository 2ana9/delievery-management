package com.ana29.deliverymanagement.order.exception;

public class OrderCancelTimeoutException extends RuntimeException {

	public OrderCancelTimeoutException() {
		super("주문 취소는 결제 후 5분 이내에만 가능합니다.");
	}
}
