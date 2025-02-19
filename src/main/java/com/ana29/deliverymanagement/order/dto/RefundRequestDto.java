package com.ana29.deliverymanagement.order.dto;

import java.util.UUID;

public record RefundRequestDto(
	UUID externalPaymentId, Long totalPrice, String reason
) {}