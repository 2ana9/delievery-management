package com.ana29.deliverymanagement.restaurant.repository;

import com.ana29.deliverymanagement.restaurant.entity.Menu;
import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import com.ana29.deliverymanagement.review.entity.Review;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class RestaurantSpecification {
    // 가게 이름으로 필터링
    public static Specification<Restaurant> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction();  // 조건이 없으면 전체 데이터를 반환
            }
            return criteriaBuilder.like(root.get("name"), "%" + name + "%");
        };
    }

    // 카테고리 ID로 필터링
    public static Specification<Restaurant> hasCategory(UUID categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();  // 조건이 없으면 전체 데이터를 반환
            }
            return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
        };
    }

    // 지역 ID로 필터링
    public static Specification<Restaurant> haslegalCode(String legalCode) {
        return (root, query, criteriaBuilder) -> {
            if (legalCode == null) {
                return criteriaBuilder.conjunction();  // 조건이 없으면 전체 데이터를 반환
            }
            return criteriaBuilder.equal(root.get("legalCode"), legalCode);
        };
    }

    public static Specification<Restaurant> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isDeleted"),false);//삭제가 안된 값들만 조회되도록

    }





}
