package com.ana29.deliverymanagement.review.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateReviewRequestDto(@NotNull UUID orderId,
                                     @NotNull String title,
                                     @NotNull String content,
                                     @NotNull int rating) {
}
