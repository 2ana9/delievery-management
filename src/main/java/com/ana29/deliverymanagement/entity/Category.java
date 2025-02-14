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
@Table(name = "p_categories")
public class Category extends Timestamped  {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id", nullable = false)
    private UUID id;

    @Column(name = "food_type", nullable = false, length = 50)
    private String food_type;

    @Column(name = "is_deleted",nullable = false)
    private boolean is_deleted =false;


    public Category(CategoryRequestDto requestDto) {
        this.food_type = requestDto.getFood_type();
    }
}