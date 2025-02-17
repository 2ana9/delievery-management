package com.ana29.deliverymanagement.order.dto;

import com.ana29.deliverymanagement.global.constant.PaymentTypeEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record CreateOrderRequestDto(@NotNull UUID menuId,
									@Positive Integer quantity,
									String orderRequest,
									@NotNull PaymentTypeEnum paymentType) {
}
