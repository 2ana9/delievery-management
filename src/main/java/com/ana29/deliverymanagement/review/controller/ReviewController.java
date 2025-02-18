package com.ana29.deliverymanagement.review.controller;

import com.ana29.deliverymanagement.review.dto.ReviewRequestDto;
import com.ana29.deliverymanagement.review.entity.Review;
import com.ana29.deliverymanagement.review.service.ReviewService;
import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 작성
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto<Review> createReview(@RequestBody ReviewRequestDto reviewRequestDto) {
        return reviewService.createReview(reviewRequestDto);
    }

    // 모든 리뷰 가져오기
    @GetMapping
    public ResponseDto<Page<Review>> getAllReviews(@RequestParam int page, @RequestParam int size) {
        return reviewService.getAllReviews(page, size);
    }

//     가게 ID로 연관된 모든 리뷰 가져오기
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseDto<Page<Review>> getReviewsByRestaurantId(@PathVariable UUID restaurantId,
                                                              @RequestParam int page,
                                                              @RequestParam int size) {
        return reviewService.getReviewsByRestaurantId(restaurantId, page, size);
    }

//     리뷰 ID로 리뷰 가져오기
    @GetMapping("/{id}")
    public ResponseDto<Review> getReviewById(@PathVariable UUID id) {
        return reviewService.getReviewById(id);
    }

    // 리뷰 수정
    @PutMapping("/{id}")
    public ResponseDto<Review> updateReview(@PathVariable UUID id, @RequestBody ReviewRequestDto reviewRequestDto) {
        return reviewService.updateReview(id, reviewRequestDto);
    }

    // 리뷰 삭제
    @DeleteMapping("/{id}")
    public ResponseDto<String> deleteReview(@PathVariable UUID id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reviewService.deleteReview(id, userDetails.getUsername());  // 리뷰 삭제 서비스 호출
    }
}
