package com.ana29.deliverymanagement.order.exception;

public class MissingUserAddressException extends RuntimeException {
	public MissingUserAddressException() {
		super("온라인 주문은 주소가 필수입니다.");
	}
}
