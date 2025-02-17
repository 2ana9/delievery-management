package com.ana29.deliverymanagement.category.repository;

import com.ana29.deliverymanagement.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
