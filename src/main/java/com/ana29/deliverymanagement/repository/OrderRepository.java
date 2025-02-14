package com.ana29.deliverymanagement.repository;

import com.ana29.deliverymanagement.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
