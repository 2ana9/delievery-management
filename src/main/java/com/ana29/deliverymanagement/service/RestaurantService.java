package com.ana29.deliverymanagement.service;

import com.ana29.deliverymanagement.dto.RestaurantRequestDto;
import com.ana29.deliverymanagement.dto.RestaurantResponseDto;
import com.ana29.deliverymanagement.entity.Restaurant;
import com.ana29.deliverymanagement.repository.RestaurantRepository;
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
