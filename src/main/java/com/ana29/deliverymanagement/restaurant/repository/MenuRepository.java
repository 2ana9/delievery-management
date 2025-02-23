package com.ana29.deliverymanagement.restaurant.repository;

import com.ana29.deliverymanagement.restaurant.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
	@Query("SELECT m FROM Menu m JOIN FETCH m.restaurant WHERE m.id = :menuId AND m.isDeleted = FALSE ")
	Optional<Menu> findMenuWithRestaurant(@Param("menuId") UUID menuId);
	Optional<Menu> findByIdAndIsDeletedFalse(UUID id);

	Page<Menu> findAllByIsDeletedFalse(Pageable pageable);

	Optional<Object> findByNameAndIsDeletedFalse(String name);

}
