package com.ana29.deliverymanagement.constant;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    PENDING("주문 완료"),
    PAID("결제 완료"),
    SHIPPED("배송 중"),
    CANCELED("취소됨");

    private final String description;

    OrderStatusEnum(String description) {
        this.description = description;
    }
}