package com.ana29.deliverymanagement.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDto {
    private String foodType;
    private boolean isDeleted; //삭제진행시 사용
}
