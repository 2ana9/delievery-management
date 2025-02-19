package com.ana29.deliverymanagement.order.exception;

import com.ana29.deliverymanagement.global.constant.OrderStatusEnum;
import com.ana29.deliverymanagement.global.exception.CustomConflictException;

public class OrderStatusChangeException extends CustomConflictException {

	public OrderStatusChangeException(OrderStatusEnum currentStatus, OrderStatusEnum newStatus) {
		super(String.format("주문 상태를 변경할 수 없습니다. (현재 상태: %s → 변경 시도 상태: %s)",
			currentStatus, newStatus));
	}
}

