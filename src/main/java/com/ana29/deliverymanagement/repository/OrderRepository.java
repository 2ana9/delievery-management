package com.ana29.deliverymanagement.repository;

import com.ana29.deliverymanagement.entity.Order;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {
  
}
