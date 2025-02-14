package com.ana29.deliverymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDto {
    private String food_type;
    private boolean is_deleted; //삭제진행시 사용
}
