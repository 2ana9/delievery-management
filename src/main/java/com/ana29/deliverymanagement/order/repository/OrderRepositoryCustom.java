package com.ana29.deliverymanagement.order.repository;

import com.ana29.deliverymanagement.order.dto.OrderHistoryResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderSearchCondition;
import com.ana29.deliverymanagement.order.entity.Order;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {

	Page<OrderHistoryResponseDto> findOrderHistory(String userId,
		OrderSearchCondition condition,
		Pageable pageable);

	Optional<Order> findOrderById(UUID orderId, String userId);
}
