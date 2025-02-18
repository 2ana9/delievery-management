package com.ana29.deliverymanagement.restaurant.service;

import com.ana29.deliverymanagement.area.entity.Area;
import com.ana29.deliverymanagement.area.repository.AreaRepository;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantRequestDto;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantResponseDto;
import com.ana29.deliverymanagement.restaurant.entity.Category;
import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import com.ana29.deliverymanagement.restaurant.repository.CategoryRepository;
import com.ana29.deliverymanagement.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    private final AreaRepository areaRepository;

    private final CategoryRepository categoryRepository;

    public RestaurantResponseDto createRestaurant(RestaurantRequestDto restaurantRequestDto) {
        Area area = areaRepository.findById(restaurantRequestDto.getArea())
                .orElseThrow(() -> new RuntimeException("Area not found"));
        Category category = categoryRepository.findById(restaurantRequestDto.getCategory())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Restaurant restaurant = restaurantRepository.save(
                Restaurant.builder()
                        .name(restaurantRequestDto.getName())
                        .ownerId(restaurantRequestDto.getOwnerId())
                        .content(restaurantRequestDto.getContent())
                        .area(area)
                        .category(category)
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

    public Page<RestaurantResponseDto> getAllRestaurant(Pageable pageable) {
        return null;
    }
}
