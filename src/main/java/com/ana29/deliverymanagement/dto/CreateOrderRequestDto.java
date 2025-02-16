package com.ana29.deliverymanagement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record CreateOrderRequestDto(@NotNull UUID menuId,
									@Positive Integer quantity,
									String orderRequest) {
}
