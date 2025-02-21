package com.ana29.deliverymanagement.restaurant.repository;

import com.ana29.deliverymanagement.restaurant.dto.RestaurantWithRatingDto;
import com.ana29.deliverymanagement.restaurant.entity.QRestaurant;
import com.ana29.deliverymanagement.review.entity.QReview;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class RestaurantRepositoryCustomImpl implements RestaurantRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public RestaurantRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<RestaurantWithRatingDto> getRestaurantsWithAverageRating(Pageable pageable) {
        QRestaurant restaurant = QRestaurant.restaurant;
        QReview review = QReview.review;

        // 기본 쿼리 생성: 가게 목록
        JPAQuery<RestaurantWithRatingDto> query = queryFactory
                .select(
                        Projections.constructor(RestaurantWithRatingDto.class,
                                restaurant.id,
                                restaurant.name,
                                restaurant.ownerId,
                                restaurant.operatingHours,
                                // 리뷰의 평점 평균 계산
                                review.rating.avg().as("averageRating")
                        ))
                .from(restaurant)
                .leftJoin(review).on(review.order.menu.restaurant.id.eq(restaurant.id)) // 리뷰와 가게 연결
                .where(restaurant.isDeleted.isFalse()) // 삭제되지 않은 가게만 조회
                .groupBy(restaurant.id);

        // 페이징 처리
        query.offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 결과 리스트 조회
        List<RestaurantWithRatingDto> results = query.fetch();

        // 페이징 처리된 결과 반환
        return new PageImpl<>(results, pageable, query.fetchCount());
    }
}
