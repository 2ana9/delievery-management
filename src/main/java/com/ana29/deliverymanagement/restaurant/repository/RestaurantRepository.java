package com.ana29.deliverymanagement.restaurant.repository;

import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID>, RestaurantRepositoryCustom, JpaSpecificationExecutor<Restaurant> {

}

