package com.ana29.deliverymanagement.order.dto;

import com.ana29.deliverymanagement.global.constant.OrderStatusEnum;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderHistoryResponseDto(UUID orderId,
									  String restaurantName,
									  String menuName,
									  OrderStatusEnum orderStatus,
									  LocalDateTime createdAt
) {}