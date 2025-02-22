package com.ana29.deliverymanagement.order.dto;

import com.ana29.deliverymanagement.global.constant.OrderStatusEnum;
import com.ana29.deliverymanagement.global.constant.OrderTypeEnum;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderHistoryResponseDto(UUID orderId,
									  UUID restaurantId,
									  String restaurantName,
									  String foodType,
									  String menuName,
									  OrderStatusEnum orderStatus,
									  OrderTypeEnum orderType,
									  LocalDateTime createdAt,
									  LocalDateTime updatedAt
) {}