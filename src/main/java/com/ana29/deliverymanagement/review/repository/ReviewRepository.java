package com.ana29.deliverymanagement.review.repository;

import com.ana29.deliverymanagement.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Optional<Review> findByOrderId(UUID orderId);

    Page<Review> findByRestaurantId(UUID restaurantId, Pageable pageable);

    // 리뷰 ID로 조회하는 메서드 (Optional로 반환)
    Optional<Review> findById(UUID id);
}
