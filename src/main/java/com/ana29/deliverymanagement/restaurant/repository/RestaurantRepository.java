package com.ana29.deliverymanagement.restaurant.repository;

import com.ana29.deliverymanagement.restaurant.dto.RestaurantWithRatingDto;
import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID>, RestaurantRepositoryCustom {
}
