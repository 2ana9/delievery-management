package com.ana29.deliverymanagement.order.exception;

import java.util.UUID;

public class MenuNotFoundException extends RuntimeException {
	public MenuNotFoundException(UUID menuId) {
		super("메뉴 ID를 찾을 수 없습니다. ID: " + menuId);
	}
}
