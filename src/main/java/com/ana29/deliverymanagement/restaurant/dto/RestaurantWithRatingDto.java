package com.ana29.deliverymanagement.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.UUID;

@Getter
@Setter
public class RestaurantWithRatingDto {
    private UUID id;
    private String name;
    private String ownerId;
    private String operatingHours;

    @JsonFormat(shape = JsonFormat.Shape.STRING) // JSON 직렬화 시 문자열로 변환
    private String averageRating; // String 타입으로 변경

    public RestaurantWithRatingDto(UUID id, String name, String ownerId, String operatingHours, Double averageRating) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.operatingHours = operatingHours;
        this.averageRating = formatRating(averageRating);
    }

    private String formatRating(Double rating) {
        if (rating == null) {
            return "0.0"; // null 방지
        }
        DecimalFormat df = new DecimalFormat("0.0"); // 소수점 한 자리까지만 표시
        df.setRoundingMode(RoundingMode.HALF_UP); // 반올림 적용
        return df.format(rating);
    }
}
