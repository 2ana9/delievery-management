package com.ana29.deliverymanagement.restaurant.dto;

import com.ana29.deliverymanagement.restaurant.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CategoryResponseDto {
    private UUID id;
    private String foodType;
    private boolean isDeleted;

    public CategoryResponseDto(Category category) {
        this.id = category.getId();
        this.foodType = category.getFoodType();
        this.isDeleted = category.isDeleted();
    }

    public static CategoryResponseDto from(Category category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getFoodType(),
                category.isDeleted()
                );
    }
}
