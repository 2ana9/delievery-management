package com.ana29.deliverymanagement.repository;

import com.ana29.deliverymanagement.entity.Order;
import java.util.Optional;

public interface OrderRepositoryCustom {

	Optional<Order> findLatestPendingOrder(String userId);
}
