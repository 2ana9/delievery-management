package com.ana29.deliverymanagement.repository;

import com.ana29.deliverymanagement.entity.Menu;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
	@Query("SELECT m FROM Menu m JOIN FETCH m.restaurant WHERE m.id = :menuId")
	Optional<Menu> findMenuWithRestaurant(@Param("menuId") UUID menuId);

}
