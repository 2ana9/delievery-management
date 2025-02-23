package com.ana29.deliverymanagement.review.controller;

import com.ana29.deliverymanagement.global.constant.OrderTypeEnum;
import com.ana29.deliverymanagement.global.constant.PaymentTypeEnum;
import com.ana29.deliverymanagement.order.dto.CreateOrderRequestDto;
import com.ana29.deliverymanagement.review.dto.CreateReviewRequestDto;
import com.ana29.deliverymanagement.review.dto.ReviewDetailResponseDto;
import com.ana29.deliverymanagement.review.dto.ReviewSearchCondition;
import com.ana29.deliverymanagement.review.dto.UpdateReviewRequestDto;
import com.ana29.deliverymanagement.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ReviewDtoStub {
    public static final UUID TEST_ORDER_ID = UUID.fromString(
            "550e8400-e29b-41d4-a716-446655440000");
    public static final UUID TEST_MENU_ID = UUID.fromString("550e8400-e29b-41d4-a716"
            + "-446655440001");
    public static final UUID TEST_RESTAURANT_ID = UUID.fromString(
            "550e8400-e29b-41d4-a716-446655440002");
    public static final UUID TEST_REVIEW_ID = UUID.fromString(
            "550e8400-e29b-41d4-a716-446655440003");
    public static final UUID TEST_EXTERNAL_PAYMENT_ID = UUID.fromString(
            "550e8400-e29b-41d4-a716-446655440004");

    public static final String TEST_USER_ID = "testuser";

    public static CreateReviewRequestDto getReviewDetailsResponseDtoStub() {
        return new CreateReviewRequestDto(
                TEST_ORDER_ID,
                "정말 맛있어요!",
                "배달도 빠르고, 양도많아요! 또 시켜먹을게요~",
                5
        );
    }

    public static UpdateReviewRequestDto createUpdateReviewRequest() {
        return new UpdateReviewRequestDto(
                "이 집 별로입니다!",
                "먹다가 머리카락나와서 리뷰 수정합니다!",
                1
        );
    }


    public static ReviewDetailResponseDto getReviewDetailsResponseDtoStub(String userId){
        return ReviewDetailResponseDto.builder()
                .reviewId(TEST_REVIEW_ID)
                .orderId(TEST_ORDER_ID)
                .menuId(TEST_MENU_ID)
                .restaurantId(TEST_RESTAURANT_ID)
                .title("정말 맛있어요!")
                .content("배달도 빠르고, 양도많아요! 또 시켜먹을게요~")
                .rating(5)
                .menuName("후라이드 치킨")
                .restaurantName("후라이드가 맛있는 치킨 집")
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static ReviewSearchCondition createReviewSearchCondition() {
        return new ReviewSearchCondition(
                "치킨",
                LocalDate.now().minusDays(7),
                LocalDate.now(),
                false
        );
    }

    public static ReviewDetailResponseDto createReviewDetailResponseDto() {
        return new ReviewDetailResponseDto(
                TEST_ORDER_ID,
                TEST_ORDER_ID,
                TEST_MENU_ID,
                TEST_RESTAURANT_ID,
                "정말 맛있어요!",
                "배달도 빠르고, 양도많아요! 또 시켜먹을게요~",
                5,
                "후라이드 치킨",
                "후라이드가 맛있는 치킨 집",
                TEST_USER_ID,
                LocalDateTime.now()
        );
    }

    public static Page<ReviewDetailResponseDto> createReviewsPage() {
        List<ReviewDetailResponseDto> reviews = List.of(createReviewDetailResponseDto());
        return new PageImpl<>(reviews, PageRequest.of(0, 10), 1);
    }

    public static MockHttpServletRequestBuilder applyDefaultSearchParams(
            MockHttpServletRequestBuilder request) {
        return request
                .param("keyword", "치킨")
                .param("startDate", "2025-02-14")
                .param("endDate", "2025-02-21")
                .param("isAsc", "false")
                .param("page", "0")
                .param("size", "10");

    }
}
