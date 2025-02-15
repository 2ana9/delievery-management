package com.ana29.deliverymanagement.repository;

import com.ana29.deliverymanagement.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
}
