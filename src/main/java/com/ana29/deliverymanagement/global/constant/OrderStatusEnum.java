package com.ana29.deliverymanagement.global.constant;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    PENDING("주문 접수"),
    PAID("결제 완료"),
    COMPLETED("주문 완료"),
    CANCELED("주문 취소");

    private final String description;

    OrderStatusEnum(String description) {
        this.description = description;
    }

    public boolean canChangeTo(OrderStatusEnum nextStatus) {
        return switch (this) {
            case PENDING -> nextStatus == PAID;
            case PAID -> nextStatus == COMPLETED || nextStatus == CANCELED;
            case COMPLETED,CANCELED -> false;
        };
    }
}