package com.ana29.deliverymanagement.restaurant.repository;

import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
}
