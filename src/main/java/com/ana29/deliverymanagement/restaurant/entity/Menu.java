package com.ana29.deliverymanagement.restaurant.entity;


import com.ana29.deliverymanagement.global.entity.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더를 통한 생성만 허용
@Builder
@Table(name = "p_menu")
public class Menu extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "menu_id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 50,  unique=true)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;


    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    public void softDelete(String deletedBy) {
        this.isDeleted = true;
        // Timestamped의 delete() 메서드를 활용하여 deletedAt, deletedBy 설정
        super.delete(deletedBy);
    }

}
