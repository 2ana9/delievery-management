package com.ana29.deliverymanagement.menu.entity;

import com.ana29.deliverymanagement.global.entity.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "p_menu")
public class Menu extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "menu_id", nullable = false)
    private UUID id;

    @Column(nullable = false, length = 50, unique=true)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(length = 500)
    private String description;

    // 가게 사장님의 ID (메뉴 소유자)
    @Column(nullable = false, length = 50)
    private String ownerId;

    // 메뉴가 속한 가게(레스토랑)의 ID
    @Column(nullable = false)
    private UUID restaurantId;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;
}
