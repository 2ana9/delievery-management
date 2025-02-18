package com.ana29.deliverymanagement.review.service;

import com.ana29.deliverymanagement.order.entity.Order;
import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import com.ana29.deliverymanagement.review.dto.ReviewRequestDto;
import com.ana29.deliverymanagement.review.entity.Review;
import com.ana29.deliverymanagement.user.entity.User;
import com.ana29.deliverymanagement.review.exception.AlreadyReviewedException;
import com.ana29.deliverymanagement.global.exception.CustomNotFoundException;
import com.ana29.deliverymanagement.order.repository.OrderRepository;
import com.ana29.deliverymanagement.restaurant.repository.RestaurantRepository;
import com.ana29.deliverymanagement.review.repository.ReviewRepository;
import com.ana29.deliverymanagement.user.repository.UserRepository;
import com.ana29.deliverymanagement.global.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public ResponseDto<Review> createReview(ReviewRequestDto reviewRequestDto) {
        log.info("createReview");

        User user = userRepository.findById(reviewRequestDto.getUserId())
                .orElseThrow(() -> new CustomNotFoundException("사용자를 찾을 수 없습니다. userId: " + reviewRequestDto.getUserId()));

        Order order = orderRepository.findById(reviewRequestDto.getOrderId())
                .orElseThrow(() -> new CustomNotFoundException("해당 주문을 찾을 수 없습니다. orderId: " + reviewRequestDto.getOrderId()));

        Restaurant restaurant = restaurantRepository.findById(reviewRequestDto.getRestaurantId())
                .orElseThrow(() -> new CustomNotFoundException("해당 가게를 찾을 수 없습니다. restaurantId: " + reviewRequestDto.getRestaurantId()));

        // 주문에 대한 리뷰가 이미 존재하는지 확인
        Optional<Review> existingReview = reviewRepository.findByOrderId(reviewRequestDto.getOrderId());
        if (existingReview.isPresent()) {
            throw new AlreadyReviewedException("이 주문에 대한 리뷰는 이미 작성되었습니다. orderId: " + reviewRequestDto.getOrderId());
        }

        Review review = Review.builder()
                .user(user)
                .order(order)
                .restaurant(restaurant)
                .title(reviewRequestDto.getTitle())
                .content(reviewRequestDto.getContent())
                .rating(reviewRequestDto.getRating())
                .build();

        Review savedReview = reviewRepository.save(review);

        // 성공적인 응답 반환
        return ResponseDto.success(savedReview);
    }


    // 모든 리뷰 가져오기
//    public ResponseDto<Page<Review>> getAllReviews(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Review> reviews = reviewRepository.findAll(pageable);
//
//        // 성공적인 응답 반환
//        return ResponseDto.success(reviews);
//    }

    // 가게에 달린 리뷰들 모두 조회
//    public ResponseDto<Page<Review>> getReviewsByRestaurantId(UUID restaurantId, int page, int size) {
//        // 존재하는 가게인지 확인
//        Restaurant restaurant = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new CustomNotFoundException("해당 가게를 찾을 수 없습니다."));
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//        Page<Review> reviews = reviewRepository.findByRestaurantId(restaurant.getId(), pageable);
//
//        return ResponseDto.success(reviews);
//    }

    // 리뷰 ID로 리뷰 가져오기
//    @Transactional(readOnly = true)
//    public ResponseDto<Review> getReviewById(UUID id) {
//        // 리뷰 조회
//        Review review = reviewRepository.findById(id)
//                .orElseThrow(() -> new CustomNotFoundException("리뷰를 찾을 수 없습니다. id: " + id));
//
//        // 조회된 리뷰를 ResponseDto로 반환
//        return ResponseDto.success(review);
//    }

    // 리뷰 수정
    @Transactional
    public ResponseDto<Review> updateReview(UUID id, ReviewRequestDto reviewRequestDto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("리뷰를 찾을 수 없습니다. id: " + id));

        review.updateReview(reviewRequestDto.getTitle(), reviewRequestDto.getContent(), reviewRequestDto.getRating());

        return ResponseDto.success(review);
    }

    // 리뷰 삭제 (소프트 삭제)
    public ResponseDto<String> deleteReview(UUID id, String userId) {
        // 리뷰 조회
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("리뷰를 찾을 수 없습니다. id: " + id));

        // 삭제 처리 (소프트 삭제)
        review.setDeletedAt(LocalDateTime.now());
        review.setDeletedBy(userId);

        // 리뷰 저장 (실제 삭제가 아니라, 삭제 상태로 업데이트)
        reviewRepository.save(review);

        return ResponseDto.success("리뷰가 성공적으로 삭제되었습니다.");
    }
}
