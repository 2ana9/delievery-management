package com.ana29.deliverymanagement.restaurant.dto;

import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class RestaurantResponseDto {
    private UUID id;
    private String ownderId;
    private UUID category;
    private UUID area;
    private String name;
    private String content;
    private String operatingHours;
    private boolean isDeleted; //삭제진행시 사용

    public RestaurantResponseDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.ownderId = restaurant.getOwnderId();
        this.category = restaurant.getCategory().getId();
        this.area = restaurant.getArea().getId();
        this.name = restaurant.getName();
        this.content = restaurant.getContent();
        this.operatingHours = restaurant.getOperatingHours();
        this.isDeleted = restaurant.isDeleted();
    }
}
