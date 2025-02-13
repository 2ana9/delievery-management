package com.ana29.deliverymanagement.entity;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    PENDING, // 진행 중
    SHIPPED, // 배송 중
    CANCELED // 취소됨
}

