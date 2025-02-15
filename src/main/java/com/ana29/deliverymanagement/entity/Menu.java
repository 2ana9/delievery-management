package com.ana29.deliverymanagement.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더를 통한 생성만 허용
@Builder
@Table(name = "p_menus")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "menu_id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "content", nullable = false, length = 100)
    private String content;

    @Column(name = "retaurant_id", nullable = false)
    private UUID restaurantId;
}
