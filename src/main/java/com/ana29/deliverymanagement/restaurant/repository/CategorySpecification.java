package com.ana29.deliverymanagement.restaurant.repository;

import com.ana29.deliverymanagement.restaurant.entity.Category;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class CategorySpecification {
    // 카테고리 ID로 필터링
    public static Specification<Category> hasId(UUID id) {
        return (root, query, criteriaBuilder) -> {
            if (id == null) {
                return criteriaBuilder.conjunction();  // 조건이 없으면 전체 데이터를 반환
            }
            return criteriaBuilder.equal(root.get("id"), id);
        };
    }

    public static Specification<Category> hasFoodType(String foodType) {
        return (root, query, criteriaBuilder) -> {
            if (foodType == null || foodType.isEmpty()) {
                return criteriaBuilder.conjunction();  // 조건이 없으면 전체 데이터를 반환
            }
            return criteriaBuilder.like(root.get("foodType"), "%" + foodType + "%"); //부분만 작성해도 조회되도록
        };
    }

    public static Specification<Category> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("isDeleted"),false);//삭제가 안된 값들만 조회되도록

    }
}
