package com.ana29.deliverymanagement.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_carts")
public class Cart extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(mappedBy = "cart")
    private User user;

    @Column(nullable = false)
    private UUID restaurantId; // 식당 ID (Foreign Key)

    @Column(nullable = false)
    private int menuQuantity; // 메뉴 수량

    @Column(nullable = false)
    private UUID menuId; // 메뉴 ID (Foreign Key)

    @Column(nullable = false)
    private UUID orderId; // 주문 ID (Foreign Key)
}
