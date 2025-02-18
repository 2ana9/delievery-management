package com.ana29.deliverymanagement.restaurant.dto;

import com.ana29.deliverymanagement.area.entity.Area;
import com.ana29.deliverymanagement.restaurant.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantRequestDto {
    private String name;
    private String ownerId; //관리자가 입력하는 가게주인id
    private UUID category;
    private UUID area;
    private String content;
    private String operatingHours;
    private boolean isDeleted; //삭제진행시 사용
}
