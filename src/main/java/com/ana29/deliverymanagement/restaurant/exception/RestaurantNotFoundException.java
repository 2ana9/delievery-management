package com.ana29.deliverymanagement.restaurant.exception;

import com.ana29.deliverymanagement.global.exception.CustomNotFoundException;
import java.util.UUID;

public class RestaurantNotFoundException extends CustomNotFoundException {
	public RestaurantNotFoundException(UUID restaurantId) {
		super("가게 ID를 찾을 수 없습니다. ID: " + restaurantId);
	}
}
