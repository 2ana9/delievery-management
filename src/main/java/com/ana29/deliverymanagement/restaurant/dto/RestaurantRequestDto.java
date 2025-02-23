package com.ana29.deliverymanagement.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantRequestDto {
    private String name;
    private String ownerId; //관리자가 입력하는 가게주인id
    private UUID category;
    private String legalCode;
    private String content;
    private String operatingHours;
    private boolean isDeleted; //삭제진행시 사용

}
