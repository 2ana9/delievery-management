package com.ana29.deliverymanagement.order.dto;

import com.ana29.deliverymanagement.order.entity.Order;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreateOrderResponseDto(UUID orderId,
									 String menuName,
									 Integer quantity,
									 Long totalPrice,
									 String orderRequest,
									 String restaurantName,
									 PaymentResultDto paymentResult) {

	public static CreateOrderResponseDto of(Order order, PaymentResultDto paymentResult) {
		return CreateOrderResponseDto.builder()
			.orderId(order.getId())
			.menuName(order.getMenu().getName())
			.totalPrice(order.getTotalPrice())
			.orderRequest(order.getOrderRequest())
			.restaurantName(order.getMenu().getRestaurant().getName())
			.paymentResult(paymentResult)
			.build();
	}

}