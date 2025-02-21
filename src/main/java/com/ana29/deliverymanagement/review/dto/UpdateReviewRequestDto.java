package com.ana29.deliverymanagement.review.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateReviewRequestDto(@NotNull String title,
                                     @NotNull String content,
                                     @NotNull int rating) {
}
