package com.ana29.deliverymanagement.entity;

import com.ana29.deliverymanagement.constant.OrderStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더를 통한 생성만 허용
@Builder
@Table(name = "p_order")
class Order extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", columnDefinition = "uuid")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatusEnum orderStatus;

    @Column(length = 100)
    private String content;

    @Column(length = 100, nullable = false)
    private String operatingHours;

    @Column(length = 100, nullable = false)
    private String orderRequest;

    @Column(length = 255, nullable = false)
    private String address;

    @Column(length = 255, nullable = false)
    private int price;

    @Column(nullable = false)
    private UUID payment_id;
}
