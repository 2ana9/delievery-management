package com.ana29.deliverymanagement.service;

import com.ana29.deliverymanagement.entity.Order;
import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import com.ana29.deliverymanagement.entity.Review;
import com.ana29.deliverymanagement.entity.User;
import com.ana29.deliverymanagement.exception.AlreadyReviewedException;
import com.ana29.deliverymanagement.exception.CustomNotFoundException;
import com.ana29.deliverymanagement.repository.OrderRepository;
import com.ana29.deliverymanagement.restaurant.repository.RestaurantRepository;
import com.ana29.deliverymanagement.repository.ReviewRepository;
import com.ana29.deliverymanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    // 리뷰 작성
    @Transactional
    public Review createReview(String userId, UUID orderId, UUID restaurantId, String title, String content, int rating) {
        log.info("createReview");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomNotFoundException("사용자를 찾을 수 없습니다. userId: " + userId));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomNotFoundException("해당 주문을 찾을 수 없습니다. orderId: " + orderId));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomNotFoundException("해당 가게를 찾을 수 없습니다. restaurantId: " + restaurantId));

        // 주문에 대한 리뷰가 이미 존재하는지 확인
        Optional<Review> existingReview = reviewRepository.findByOrderId(orderId);
        if (existingReview.isPresent()) {
            throw new AlreadyReviewedException("이 주문에 대한 리뷰는 이미 작성되었습니다. orderId: " + orderId);
        }

        Review review = Review.builder()
                .user(user)
                .order(order)
                .restaurant(restaurant)
                .title(title)
                .content(content)
                .rating(rating)
                .build();

        return reviewRepository.save(review);
    }
}
