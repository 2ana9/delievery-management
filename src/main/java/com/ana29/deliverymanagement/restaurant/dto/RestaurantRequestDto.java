package com.ana29.deliverymanagement.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantRequestDto {
    private String name;
    private String content;
    private String operatingHours;
}
