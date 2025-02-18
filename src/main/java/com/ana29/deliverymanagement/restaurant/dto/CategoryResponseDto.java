package com.ana29.deliverymanagement.restaurant.dto;

import com.ana29.deliverymanagement.restaurant.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

public record CategoryResponseDto(
        UUID id,
        String foodType,
        boolean isDeleted
) {
    public static CategoryResponseDto from(Category category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getFoodType(),
                category.isDeleted()
                );
    }
}
