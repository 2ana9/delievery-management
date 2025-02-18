package com.ana29.deliverymanagement.restaurant.dto;

import com.ana29.deliverymanagement.restaurant.entity.Restaurant;

import java.util.UUID;

public record RestaurantResponseDto(
        UUID id,
        String ownderId,
        UUID category,
        UUID area,
        String name,
        String content,
        String operatingHours,
        boolean isDeleted
) {
    public static RestaurantResponseDto from(Restaurant restaurant) {
        return new RestaurantResponseDto(
                restaurant.getId(),
                restaurant.getOwnerId(),
                restaurant.getCategory().getId(),
                restaurant.getArea().getId(),
                restaurant.getName(),
                restaurant.getContent(),
                restaurant.getOperatingHours(),
                restaurant.isDeleted()
        );
    }
}
