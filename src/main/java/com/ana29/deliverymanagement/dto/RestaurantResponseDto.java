package com.ana29.deliverymanagement.dto;

import com.ana29.deliverymanagement.entity.Restaurant;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class RestaurantResponseDto {
    private UUID id;
    private String name;
    private String content;
    private String operatingHours;

    public RestaurantResponseDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.content = restaurant.getContent();
        this.operatingHours = restaurant.getOperatingHours();
    }
}
