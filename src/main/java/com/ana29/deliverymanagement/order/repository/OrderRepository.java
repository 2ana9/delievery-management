package com.ana29.deliverymanagement.order.repository;

import com.ana29.deliverymanagement.order.entity.Order;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID>, OrderRepositoryCustom {

	@EntityGraph(attributePaths = "payment")
	Optional<Order> findWithPaymentById(UUID uuid);
}
