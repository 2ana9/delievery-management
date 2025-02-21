package com.ana29.deliverymanagement.review.repository;

import com.ana29.deliverymanagement.review.dto.ReviewDetailResponseDto;
import com.ana29.deliverymanagement.review.dto.ReviewSearchCondition;
import com.ana29.deliverymanagement.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepositoryCustom {
    Optional<Review> findReviewById(UUID reviewId, String userId);

    Page<ReviewDetailResponseDto> findReviewByRestaurantId(UUID restaurantId, ReviewSearchCondition condition, Pageable pageable);


    Page<ReviewDetailResponseDto> findAllReviews(ReviewSearchCondition condition, Pageable pageable);
}
