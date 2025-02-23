package com.ana29.deliverymanagement.restaurant.entity;

import com.ana29.deliverymanagement.global.entity.Timestamped;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantRequestDto;
import com.ana29.deliverymanagement.review.entity.Review;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
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

    @JsonIgnore
    private Double ratingAverage;

    public void update(RestaurantRequestDto restaurantRequestDto) {
        if (restaurantRequestDto.getName() != null) {
            this.name = restaurantRequestDto.getName();
        }
        if (restaurantRequestDto.getContent() != null) {
            this.content = restaurantRequestDto.getContent();
        }
        if (restaurantRequestDto.getOwnerId() != null) {
            this.ownerId = restaurantRequestDto.getOwnerId();
        }
        if (restaurantRequestDto.getOperatingHours() != null) {
            this.operatingHours = restaurantRequestDto.getOperatingHours();
        }
        if (restaurantRequestDto.isDeleted() != this.isDeleted) {
            this.isDeleted = restaurantRequestDto.isDeleted();
        }
    }


    // 명시적으로 Setter 메서드 추가
    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    //카테고리 외래키
    @Setter
    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    //지역 코드 정보 저장
    private String legalCode;

    public boolean isOwner (String userId){
        return userId.equals(ownerId);
    }

}
