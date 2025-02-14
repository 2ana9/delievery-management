package com.ana29.deliverymanagement.controller;

import com.ana29.deliverymanagement.dto.ReviewRequestDto;
import com.ana29.deliverymanagement.entity.Review;
import com.ana29.deliverymanagement.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 작성
    @PostMapping("/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public Review createReview(@RequestBody ReviewRequestDto reviewRequestDto) {
        return reviewService.createReview(
                reviewRequestDto.getUserId(),
                reviewRequestDto.getOrderId(),
                reviewRequestDto.getRestaurantId(),
                reviewRequestDto.getTitle(),
                reviewRequestDto.getContent(),
                reviewRequestDto.getRating()
        );
    }

//    // 특정 주문에 대한 리뷰 조회
//    @GetMapping("/order/{orderId}")
//    public Review getReviewByOrderId(@PathVariable UUID orderId) {
//        return reviewService.getReviewByOrderId(orderId);
//    }
}
