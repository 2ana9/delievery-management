package com.ana29.deliverymanagement.restaurant.service;

import com.ana29.deliverymanagement.restaurant.dto.RestaurantRequestDto;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantResponseDto;
import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import com.ana29.deliverymanagement.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

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

    public RestaurantResponseDto updateRestaurant(UUID id, RestaurantRequestDto restaurantRequestDto) {
        Restaurant restaurant =  restaurantRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("Restaurant not found")); //고유id값으로 가게정보 찾기
        restaurant.update(restaurantRequestDto);
//        restaurant.setUpdatedAt(LocalDateTime.now()); //수정시간 업데이트
//        restaurant.setUpdatedBy();//수정자 이름입력
        return new RestaurantResponseDto(restaurant);
    }
}
