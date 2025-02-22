package com.ana29.deliverymanagement.order.dto;

import com.ana29.deliverymanagement.global.constant.OrderStatusEnum;
import com.ana29.deliverymanagement.global.constant.OrderTypeEnum;
import com.ana29.deliverymanagement.global.constant.PaymentStatusEnum;
import com.ana29.deliverymanagement.global.constant.PaymentTypeEnum;
import com.ana29.deliverymanagement.order.entity.Order;
import com.ana29.deliverymanagement.order.entity.Payment;
import com.ana29.deliverymanagement.user.dto.GetUserAddressesResponseDto;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record OrderDetailResponseDto(
	UUID orderId,
	OrderStatusEnum orderStatus,
	Integer quantity,
	String orderRequest,
	OrderTypeEnum orderType,
	GetUserAddressesResponseDto addressInfo,
	LocalDateTime orderedAt,
	LocalDateTime updatedAt,
	UUID restaurantId,
	String restaurantName,
	String menuName,
	Long menuPrice,
	Long totalPrice,
	PaymentStatusEnum paymentStatus,
	PaymentTypeEnum paymentType,
	UUID externalPaymentId,
	LocalDateTime paidAt,
	LocalDateTime refundedAt,
	String createdBy
) {
	public static OrderDetailResponseDto from(Order order, Payment payment) {
		return OrderDetailResponseDto.builder()
			.orderId(order.getId())
			.orderStatus(order.getOrderStatus())
			.quantity(order.getQuantity())
			.orderRequest(order.getOrderRequest())
			.orderType(order.getOrderType())
			.addressInfo(GetUserAddressesResponseDto.from(order.getUserAddress()))
			.orderedAt(order.getCreatedAt())
			.updatedAt(order.getUpdatedAt())
			.restaurantId(order.getMenu().getRestaurant().getId())
			.restaurantName(order.getMenu().getRestaurant().getName())
			.menuName(order.getMenu().getName())
			.menuPrice(order.getMenu().getPrice())
			.totalPrice(order.getTotalPrice())
			.paymentStatus(payment.getPaymentStatus())
			.paymentType(payment.getPaymentType())
			.externalPaymentId(payment.getExternalPaymentId())
			.paidAt(payment.getCreatedAt())
			.refundedAt(payment.getRefundedAt())
			.createdBy(order.getCreatedBy())
			.build();
	}
}