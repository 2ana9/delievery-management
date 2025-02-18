package com.ana29.deliverymanagement.restaurant.entity;

import com.ana29.deliverymanagement.area.entity.Area;
import com.ana29.deliverymanagement.global.entity.Timestamped;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    //가게주인을 표시? 표기? 하는 컬럼
    @Column(nullable = false)
    private String ownerId;

    @Column(length = 100)
    private String content;

    @Column(length = 100, nullable = false)
    private String operatingHours;

    @Column(name = "is_deleted",nullable = false)
    @Builder.Default
    private boolean isDeleted =false;

    public void update(RestaurantRequestDto restaurantRequestDto) {
        this.name = restaurantRequestDto.getName();
        this.content = restaurantRequestDto.getContent();
        this.ownerId = restaurantRequestDto.getOwnerId();
        this.operatingHours = restaurantRequestDto.getOperatingHours();
        this.isDeleted = restaurantRequestDto.isDeleted();
    }

    // 명시적으로 Setter 메서드 추가
    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    //카테고리 외래키
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    //주소 외래키
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

}
