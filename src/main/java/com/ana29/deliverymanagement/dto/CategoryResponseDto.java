package com.ana29.deliverymanagement.dto;

import com.ana29.deliverymanagement.entity.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CategoryResponseDto {
    private UUID id;
    private String foodType;
    private boolean isDeleted;

    public CategoryResponseDto(Category category) {
        this.id = category.getId();
        this.foodType = category.getFoodType();
        this.isDeleted = category.isDeleted();
    }
}
