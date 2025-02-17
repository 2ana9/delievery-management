package com.ana29.deliverymanagement.restaurant.entity;

import com.ana29.deliverymanagement.global.entity.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더를 통한 생성만 허용
@Builder
@Table(name = "p_restaurant")
public class Restaurant extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "restaurant_id", columnDefinition = "uuid")
    private UUID id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100)
    private String content;

    @Column(length = 100, nullable = false)
    private String operatingHours;

//    @OneToMany
//    @JoinColumn(name = "category_id")
//    private List<Category> alists = new ArrayList<>();



}
