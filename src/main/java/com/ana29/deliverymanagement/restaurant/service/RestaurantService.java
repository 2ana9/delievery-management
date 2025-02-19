package com.ana29.deliverymanagement.restaurant.service;

import com.ana29.deliverymanagement.area.entity.Area;
import com.ana29.deliverymanagement.area.repository.AreaRepository;
import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantRequestDto;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantResponseDto;
import com.ana29.deliverymanagement.restaurant.entity.Category;
import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import com.ana29.deliverymanagement.restaurant.repository.CategoryRepository;
import com.ana29.deliverymanagement.restaurant.repository.RestaurantRepository;
import com.ana29.deliverymanagement.restaurant.repository.RestaurantSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    private final AreaRepository areaRepository;

    private final CategoryRepository categoryRepository;

    @Transactional
    public ResponseDto<Restaurant> createRestaurant(RestaurantRequestDto requestDto) {
        Area area = areaRepository.findById(requestDto.getArea())
                .orElseThrow(() -> new RuntimeException("Area not found"));
        Category category = categoryRepository.findById(requestDto.getCategory())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Restaurant restaurant = restaurantRepository.save(
                Restaurant.builder()
                        .name(requestDto.getName())
                        .ownerId(requestDto.getOwnerId())
                        .content(requestDto.getContent())
                        .area(area)
                        .category(category)
                        .operatingHours(requestDto.getOperatingHours())
                        .build()
        );

        return ResponseDto.success(restaurant);
    }

    @Transactional
    public ResponseDto<Restaurant> updateRestaurant(UUID id, RestaurantRequestDto requestDto,String userId) {
        Restaurant restaurant =  restaurantRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("Restaurant not found")); //고유id값으로 가게정보 찾기
        restaurant.update(requestDto);
        restaurant.setUpdatedAt(LocalDateTime.now());
        restaurant.setUpdatedBy(userId);

        return ResponseDto.success(restaurant);
    }

    public Page<RestaurantResponseDto> getAllRestaurant(Pageable pageable) {
        return restaurantRepository.findAll(pageable).map(RestaurantResponseDto::from);
    }

    public ResponseDto<Restaurant> deleteRestaurant(UUID id,String userId) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("restaurant not found"));
        restaurant.setIsDeleted(true);
        restaurant.setUpdatedAt(LocalDateTime.now());
        restaurant.setUpdatedBy(userId);
        restaurantRepository.save(restaurant);

        return ResponseDto.success(restaurant);
    }

    @Transactional
    public ResponseDto<List<Restaurant>> searchRestaurants(String name, UUID categoryId, Long areaId, Pageable pageable) {
        //가게이름,음식카테고리,지역위치로 필터링 진행 (+ 페이징처리)
        // 동적 쿼리 조건 생성
        Specification<Restaurant> spec = Specification.where(RestaurantSpecification.hasName(name))
                .and(RestaurantSpecification.hasCategory(categoryId))
                .and(RestaurantSpecification.hasArea(areaId));

        // 조건에 맞는 데이터를 페이징 처리하여 가져오기
        Page<Restaurant> restaurantPage = restaurantRepository.findAll(spec, pageable);

        return ResponseDto.success(restaurantPage.getContent());
    };
}
