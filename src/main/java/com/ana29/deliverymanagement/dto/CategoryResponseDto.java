package com.ana29.deliverymanagement.dto;

import com.ana29.deliverymanagement.entity.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CategoryResponseDto {
    private UUID id;
    private String food_type;
    private boolean is_deleted;

    public CategoryResponseDto(Category category) {
        this.id = category.getId();
        this.food_type = category.getFood_type();
        this.is_deleted = category.is_deleted();//boolean 타입은 get메서드 자동생성x
    }
}
