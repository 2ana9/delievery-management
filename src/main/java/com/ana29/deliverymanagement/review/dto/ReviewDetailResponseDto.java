package com.ana29.deliverymanagement.review.dto;

import com.ana29.deliverymanagement.review.entity.Review;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ReviewDetailResponseDto(
        UUID reviewId,
        UUID orderId,
        UUID menuId,
        UUID restaurantId,
        String title,
        String content,
        int rating,
        String menuName,
        String restaurantName,
        String createdBy,
        LocalDateTime createdAt
) {
    public static ReviewDetailResponseDto from(Review review) {
        return ReviewDetailResponseDto.builder()
                .reviewId(review.getId())
                .orderId(review.getOrder().getId())
                .menuId(review.getOrder().getMenu().getId())
                .restaurantId(review.getOrder().getMenu().getRestaurant().getId())
                .title(review.getTitle())
                .content(review.getContent())
                .rating(review.getRating())
                .menuName(review.getOrder().getMenu().getName())
                .restaurantName(review.getOrder().getMenu().getRestaurant().getName())
                .createdBy(review.getCreatedBy())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
