package com.ana29.deliverymanagement.repository;

import com.ana29.deliverymanagement.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRespository extends JpaRepository<Restaurant, Integer> {
}
