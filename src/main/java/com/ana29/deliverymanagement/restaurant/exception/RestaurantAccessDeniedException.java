package com.ana29.deliverymanagement.restaurant.exception;

import com.ana29.deliverymanagement.global.exception.CustomAccessDeniedException;
import java.util.UUID;

public class RestaurantAccessDeniedException extends CustomAccessDeniedException {

	public RestaurantAccessDeniedException(UUID restaurantId) {
		super("접근 권한이 없습니다. ID: " + restaurantId);
	}
}
