package com.ana29.deliverymanagement.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreatePaymentRequestDto (@NotNull UUID orderId,
									   String paymentType) {

}
