package com.ana29.deliverymanagement.dto;

import java.util.UUID;

public record CreateOrderResponseDto (UUID orderId, Long totalPrice) {
}
