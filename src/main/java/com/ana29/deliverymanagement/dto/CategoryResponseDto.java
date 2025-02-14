package com.ana29.deliverymanagement.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CategoryResponseDto {
    private UUID id;
    private String food_type;
    private boolean is_deleted;
}
