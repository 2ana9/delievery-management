package com.ana29.deliverymanagement.restaurant.service;

import com.ana29.deliverymanagement.area.repository.AreaRepository;
import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.restaurant.dto.PaginationDto;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantReponseDto;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantRequestDto;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    private final AreaRepository areaRepository;

    private final CategoryRepository categoryRepository;

    @Transactional
    public ResponseDto<Restaurant> createRestaurant(RestaurantRequestDto requestDto) {
        Category category = categoryRepository.findById(requestDto.getCategory())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Restaurant restaurant = restaurantRepository.save(
                Restaurant.builder()
                        .name(requestDto.getName())
                        .ownerId(requestDto.getOwnerId())
                        .content(requestDto.getContent())
                        .legalCode(requestDto.getLegalCode())
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
        Optional<Category> categoryOptional = categoryRepository.findById(requestDto.getCategory());
        Category category = categoryOptional.get();
        restaurant.setCategory(category);
        restaurant.update(requestDto);
        restaurant.setUpdatedAt(LocalDateTime.now());
        restaurant.setUpdatedBy(userId);
        restaurantRepository.save(restaurant);

        return ResponseDto.success(restaurant);
    }

    public ResponseDto<Restaurant> deleteRestaurant(UUID id,String userId) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("restaurant not found"));
        restaurant.setIsDeleted(true);
        restaurant.setDeletedAt(LocalDateTime.now());
        restaurant.setDeletedBy(userId);
        restaurantRepository.save(restaurant);

        return ResponseDto.success(restaurant);
    }

    @Transactional
    public ResponseDto<List<Object>> searchRestaurants(
            String name, UUID categoryId, String legalCode, Pageable pageable) {
        //가게이름,음식카테고리,지역위치로 필터링 진행 (+ 페이징처리 / 삭제처리된 가게의경우 숨김)

        // 동적 쿼리 조건 생성
        Specification<Restaurant> spec = Specification.where(RestaurantSpecification.hasName(name))
                .and(RestaurantSpecification.hasCategory(categoryId))
                .and(RestaurantSpecification.haslegalCode(legalCode))
                .and(RestaurantSpecification.isNotDeleted());

        // 조건에 맞는 데이터를 페이징 처리하여 가져오기
        Page<Restaurant> restaurantPage = restaurantRepository.findAll(spec, pageable);
        List<RestaurantReponseDto> restaurantResponseList = new ArrayList<>();

        // 각 Restaurant에 대해 평균 평점 계산
        for (Restaurant restaurant : restaurantPage) {
            // restaurantId를 사용해서 평균 평점을 계산
            Double averageRating = restaurantRepository.getRestaurantAverageRating(restaurant.getId());
            if (averageRating == null){
                averageRating = 0.0;
            }
            RestaurantReponseDto responseDto = new RestaurantReponseDto();
            responseDto.setName(restaurant.getName());
            responseDto.setOwnerId(restaurant.getOwnerId());
            responseDto.setContent(restaurant.getContent());
            responseDto.setCategory(restaurant.getCategory().getId());
            responseDto.setLegalCode(restaurant.getLegalCode());
            responseDto.setOperatingHours(restaurant.getOperatingHours());
            responseDto.setDeleted(restaurant.isDeleted());
            responseDto.setRatingAverage(averageRating);

            restaurantResponseList.add(responseDto);
        }


        // 페이징 정보 생성
        PaginationDto pagination = new PaginationDto(
                restaurantPage.getTotalElements(),
                restaurantPage.getTotalPages(),
                restaurantPage.getNumber(),
                restaurantPage.getSize(),
                restaurantPage.isFirst(),
                restaurantPage.isLast(),
                restaurantPage.getNumberOfElements(),
                restaurantPage.isEmpty()
        );

        // Restaurant 리스트와 PaginationDto를 하나의 리스트에 담기
        List<Object> responseData = new ArrayList<>(restaurantResponseList);
        responseData.add(pagination);

        return ResponseDto.success(responseData);
    };
}
