package com.ana29.deliverymanagement.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantReponseDto {
    private String name;
    private String ownerId; //관리자가 입력하는 가게주인id
    private UUID category;
    private String legalCode;
    private String content;
    private String operatingHours;
    private boolean isDeleted; //삭제진행시 사용
    private Double ratingAverage;


}
