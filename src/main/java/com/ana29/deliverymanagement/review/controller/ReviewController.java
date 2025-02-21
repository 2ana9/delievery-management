package com.ana29.deliverymanagement.review.controller;

import com.ana29.deliverymanagement.review.dto.*;
import com.ana29.deliverymanagement.review.entity.Review;
import com.ana29.deliverymanagement.review.service.ReviewService;
import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 작성
    @PostMapping
    public ResponseEntity<ResponseDto<ReviewDetailResponseDto>> createReview(
            @RequestBody @Valid CreateReviewRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ReviewDetailResponseDto response =
                reviewService.createReview(requestDto, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success(HttpStatus.CREATED, response));
    }

    // 리뷰 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<ReviewDetailResponseDto>> getReviewDetail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") UUID id) {

        ReviewDetailResponseDto response =
                reviewService.getReviewDetail(id, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, response));
    }


    // 모든 리뷰 가져오기
    @GetMapping
    public ResponseEntity<ResponseDto<Page<ReviewDetailResponseDto>>> getAllReviews(
            @ModelAttribute ReviewSearchCondition condition,
            Pageable pageable) {

        Page<ReviewDetailResponseDto> response = reviewService.getAllReviews(condition, pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, response));
    }


    // 가게 ID로 연관된 모든 리뷰 가져오기 (페이징 및 검색 조건 처리)
    @GetMapping("/restaurant")
    public ResponseEntity<ResponseDto<Page<ReviewDetailResponseDto>>> getReviewsByRestaurant(
            @RequestParam UUID restaurantId,
            @ModelAttribute ReviewSearchCondition condition,
            Pageable pageable) {

        Page<ReviewDetailResponseDto> response = reviewService.getReviewsByRestaurant(restaurantId, condition, pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, response));
    }

    // 리뷰 수정
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<ReviewDetailResponseDto>> updateReview(
            @RequestBody @Valid UpdateReviewRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") UUID id) {

        ReviewDetailResponseDto response =
                reviewService.updateReview(id, requestDto, userDetails);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, response));
    }



    // 리뷰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<ReviewDetailResponseDto>> deleteReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") UUID id) {

        reviewService.deleteReview(id, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ResponseDto.success(HttpStatus.NO_CONTENT, null));
    }


}
