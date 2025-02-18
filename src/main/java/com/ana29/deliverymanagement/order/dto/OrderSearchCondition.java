package com.ana29.deliverymanagement.order.dto;

import com.ana29.deliverymanagement.global.constant.OrderStatusEnum;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record OrderSearchCondition(String keyword,
								   List<OrderStatusEnum> statuses,
								   LocalDate startDate,
								   LocalDate endDate,
								   Boolean isAsc) {
	public OrderSearchCondition {
		statuses = statuses != null ? statuses : new ArrayList<>();
		isAsc = isAsc != null ? isAsc : false;
	}
}