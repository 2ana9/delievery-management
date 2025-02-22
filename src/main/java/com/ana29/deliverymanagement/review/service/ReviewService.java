package com.ana29.deliverymanagement.review.service;

import com.ana29.deliverymanagement.order.entity.Order;
import com.ana29.deliverymanagement.order.exception.OrderNotFoundException;
import com.ana29.deliverymanagement.order.repository.OrderRepository;
import com.ana29.deliverymanagement.restaurant.repository.RestaurantRepository;
import com.ana29.deliverymanagement.review.dto.CreateReviewRequestDto;
import com.ana29.deliverymanagement.review.dto.ReviewDetailResponseDto;
import com.ana29.deliverymanagement.review.dto.ReviewSearchCondition;
import com.ana29.deliverymanagement.review.dto.UpdateReviewRequestDto;
import com.ana29.deliverymanagement.review.entity.Review;
import com.ana29.deliverymanagement.review.exception.ReviewAccessDeniedException;
import com.ana29.deliverymanagement.review.repository.ReviewRepository;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.user.constant.UserRoleEnum;
import com.ana29.deliverymanagement.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    // 리뷰 작성
    @Transactional
    public ReviewDetailResponseDto createReview(CreateReviewRequestDto requestDto, String userId) {
        Order order = findOrder(requestDto.orderId(), userId);
        Review review = createReview(requestDto, userId, order);

        return ReviewDetailResponseDto.from(review);
    }

    private Order findOrder(UUID orderId, String userId) {
        return orderRepository.findOrderById(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private Review createReview(CreateReviewRequestDto requestDto, String userId, Order order) {
        return reviewRepository
                .save(Review.of(userRepository.getReferenceById(userId), order, requestDto));
    }

    @Transactional(readOnly = true)
    public ReviewDetailResponseDto getReviewDetail(UUID reviewId, String userId) {
        Review review = findReview(reviewId, userId);
        return ReviewDetailResponseDto.from(review);
    }

    private Review findReview(UUID reviewId, String userId) {
        return reviewRepository.findReviewById(reviewId, userId)
                .orElseThrow(() -> new OrderNotFoundException(reviewId));
    }

    @Transactional
    public void deleteReview(UUID id, String userId) {
        reviewRepository.findById(id)
                .ifPresent(review -> review.delete(userId));
    }

    @Transactional(readOnly = true)
    public Page<ReviewDetailResponseDto> getReviewsByRestaurant(UUID restaurantId, ReviewSearchCondition condition, Pageable pageable) {

        return reviewRepository.findReviewByRestaurantId(restaurantId, condition, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ReviewDetailResponseDto> getAllReviews(ReviewSearchCondition condition, Pageable pageable) {
        return reviewRepository.findAllReviews(condition, pageable);
    }

    @Transactional
    public ReviewDetailResponseDto updateReview(UUID reviewId, UpdateReviewRequestDto requestDto, UserDetailsImpl userDetails) {
        Review review = findReview(reviewId, userDetails.getUsername());
        validateReviewAccess(userDetails, review);
        review.updateReview(requestDto.title(), requestDto.content(), requestDto.rating());

        return ReviewDetailResponseDto.from(review);
    }

    private void validateReviewAccess(UserDetailsImpl userDetails, Review review) {
        if(!review.getUser().getId().equals(userDetails.getUsername()) && !isAdmin(userDetails)){
            throw new ReviewAccessDeniedException(review.getId());
        }
    }

    private boolean isAdmin(UserDetailsImpl userDetails) {
        UserRoleEnum role = userDetails.getRole();
        return role.equals(UserRoleEnum.MASTER);
    }
}
