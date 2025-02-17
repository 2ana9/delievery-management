package com.ana29.deliverymanagement.restaurant.service;

import com.ana29.deliverymanagement.restaurant.dto.RestaurantRequestDto;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantResponseDto;
import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import com.ana29.deliverymanagement.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantResponseDto createRestaurant(RestaurantRequestDto restaurantRequestDto) {
        Restaurant restaurant = restaurantRepository.save(
                Restaurant.builder()
                        .name(restaurantRequestDto.getName())
                        .content(restaurantRequestDto.getContent())
                        .operatingHours(restaurantRequestDto.getOperatingHours())
                        .build()
        );
        return new RestaurantResponseDto(restaurant);
    }
}
