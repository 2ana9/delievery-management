package com.ana29.deliverymanagement.restaurant.repository;

import co.elastic.clients.elasticsearch.ml.Page;
import com.ana29.deliverymanagement.restaurant.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.awt.print.Pageable;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID>, JpaSpecificationExecutor<Category> {
}
