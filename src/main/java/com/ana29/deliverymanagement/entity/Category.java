package com.ana29.deliverymanagement.entity;

import com.ana29.deliverymanagement.dto.CategoryRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더를 통한 생성만 허용
@Builder
@Table(name = "p_category")
public class Category extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id", nullable = false)
    private UUID id;

    @Column(name = "food_type", nullable = false, length = 50)
    private String foodType;

    @Column(name = "is_deleted",nullable = false)
    @Builder.Default
    private boolean isDeleted =false;

    public void update(CategoryRequestDto requestDto) {
        this.foodType = requestDto.getFoodType();
    }

    // 명시적으로 Setter 메서드 추가
    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @ManyToOne
    private Restaurant restaurant;

}