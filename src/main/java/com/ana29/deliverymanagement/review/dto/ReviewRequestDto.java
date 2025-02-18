package com.ana29.deliverymanagement.review.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReviewRequestDto {

    private String userId;      // 유저 ID
    private UUID orderId;       // 주문 ID
    private UUID restaurantId;  // 가게 ID
    private String title;       // 리뷰 제목
    private String content;     // 리뷰 내용
    private int rating;         // 평점
}
