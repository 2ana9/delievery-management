package com.ana29.deliverymanagement.order.dto;

import com.ana29.deliverymanagement.global.constant.OrderStatusEnum;
import com.ana29.deliverymanagement.global.constant.PaymentStatusEnum;
import com.ana29.deliverymanagement.global.constant.PaymentTypeEnum;
import com.ana29.deliverymanagement.order.entity.Order;
import com.ana29.deliverymanagement.order.entity.Payment;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderDetailResponseDto(
	UUID orderId,
	OrderStatusEnum orderStatus,
	Integer quantity,
	String orderRequest,
	LocalDateTime orderedAt,
	Long totalPrice,
	String menuName,
	Long menuPrice,
	String restaurantName,
	PaymentStatusEnum paymentStatus,
	PaymentTypeEnum paymentType,
	UUID externalPaymentId,
	LocalDateTime paidAt
) {
	public static OrderDetailResponseDto from(Order order, Payment payment) {
		return new OrderDetailResponseDto(
			order.getId(),
			order.getOrderStatus(),
			order.getQuantity(),
			order.getOrderRequest(),
			order.getCreatedAt(),
			order.getTotalPrice(),
			order.getMenu().getName(),
			order.getMenu().getPrice(),
			order.getMenu().getRestaurant().getName(),
			payment != null ? payment.getPaymentStatus() : null,
			payment != null ? payment.getPaymentType() : null,
			payment != null ? payment.getExternalPaymentId() : null,
			payment != null ? payment.getCreatedAt() : null
		);
	}
}