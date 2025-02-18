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
	String restaurantName,
	String menuName,
	Long menuPrice,
	Long totalPrice,
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
			order.getMenu().getRestaurant().getName(),
			order.getMenu().getName(),
			order.getMenu().getPrice(),
			order.getTotalPrice(),
			payment.getPaymentStatus(),
			payment.getPaymentType(),
			payment.getExternalPaymentId(),
			payment.getCreatedAt()
		);
	}
}