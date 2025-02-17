package com.ana29.deliverymanagement.restaurant.repository;

import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRespository extends JpaRepository<Restaurant, Integer> {
}
