package com.ana29.deliverymanagement.restaurant.exception;

import com.ana29.deliverymanagement.global.exception.CustomNotFoundException;
import java.util.UUID;

public class MenuNotFoundException extends CustomNotFoundException {
	public MenuNotFoundException(UUID menuId) {
		super("메뉴 ID를 찾을 수 없습니다. ID: " + menuId);
	}
}
