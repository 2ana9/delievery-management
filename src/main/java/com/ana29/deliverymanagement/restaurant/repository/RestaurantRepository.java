package com.ana29.deliverymanagement.restaurant.repository;

import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID>, JpaSpecificationExecutor<Restaurant> {
    @Query("SELECT AVG(rv.rating) AS avg_rating FROM Review rv JOIN Order o ON rv.order.id = o.id JOIN Menu m ON o.menu.id = m.id JOIN Restaurant rt ON m.restaurant.id = rt.id WHERE rt.id = :restaurantId")
    public Double getRestaurantAverageRating(@Param("restaurantId") UUID restaurantId);
}

