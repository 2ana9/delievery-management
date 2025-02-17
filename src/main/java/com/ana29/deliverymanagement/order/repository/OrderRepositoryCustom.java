package com.ana29.deliverymanagement.order.repository;

import com.ana29.deliverymanagement.order.entity.Order;
import java.util.Optional;

public interface OrderRepositoryCustom {

	Optional<Order> findLatestPendingOrder(String userId);
}
