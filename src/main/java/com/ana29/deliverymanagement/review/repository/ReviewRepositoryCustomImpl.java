package com.ana29.deliverymanagement.review.repository;

import com.ana29.deliverymanagement.order.entity.QOrder;
import com.ana29.deliverymanagement.restaurant.entity.QMenu;
import com.ana29.deliverymanagement.restaurant.entity.QRestaurant;
import com.ana29.deliverymanagement.review.dto.ReviewDetailResponseDto;
import com.ana29.deliverymanagement.review.dto.ReviewSearchCondition;
import com.ana29.deliverymanagement.review.entity.QReview;
import com.ana29.deliverymanagement.review.entity.Review;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    QReview review = QReview.review;
    QMenu menu = QMenu.menu;
    QRestaurant restaurant = QRestaurant.restaurant;

    @Override
    public Page<ReviewDetailResponseDto> findAllReviews(ReviewSearchCondition condition, Pageable pageable) {
        // QReview, QMenu, QRestaurant, QOrder 객체 생성 (이전에 정의되어야 함)
        QReview review = QReview.review;
        QMenu menu = QMenu.menu;
        QRestaurant restaurant = QRestaurant.restaurant;
        QOrder order = QOrder.order;

        // 기본적으로 페이지 크기 10으로 고정, condition에서 크기가 지정되면 변경
        int pageSize = pageable.getPageSize() > 0 ? pageable.getPageSize() : 10;

        // Review 조회 쿼리
        List<ReviewDetailResponseDto> content = queryFactory
                .select(Projections.constructor(ReviewDetailResponseDto.class,
                        review.id,
                        order.id,
                        menu.id,
                        restaurant.id,
                        review.title,
                        review.content,
                        review.rating,
                        menu.name,
                        restaurant.name,
                        review.createdBy,
                        review.createdAt
                ))
                .from(review)
                .join(review.order, order)
                .join(order.menu, menu)
                .join(menu.restaurant, restaurant)
                .where(
                        review.isDeleted.isFalse(),  // 삭제되지 않은 리뷰만 필터링
                        keywordContains(condition.keyword()),  // 제목, 내용, 작성자로 검색 가능
                        createdAtBetween(condition.startDate(), condition.endDate())  // 날짜 범위 필터링
                )
                .orderBy(
                        condition.isAsc() ? review.createdAt.asc() : review.createdAt.desc(),  // 기본 정렬: 생성일 기준
                        condition.isAsc() ? review.updatedAt.asc() : review.updatedAt.desc()   // 추가 정렬: 수정일 기준
                )
                .offset(pageable.getOffset())  // 페이지 오프셋 적용
                .limit(pageSize)  // 페이지 크기 제한
                .fetch();  // 결과를 리스트로 가져옴

        // 총 레코드 수를 구하는 쿼리 (페이징에 필요)
        Long total = queryFactory
                .select(review.count())
                .from(review)
                .join(review.order, order)
                .join(order.menu, menu)
                .join(menu.restaurant, restaurant)
                .where(
                        review.isDeleted.isFalse(),
                        keywordContains(condition.keyword()),
                        createdAtBetween(condition.startDate(), condition.endDate())
                )
                .fetchOne();

        // 총 레코드 수가 null일 경우 0으로 처리
        total = total != null ? total : 0;

        // PageImpl을 사용하여 페이징 정보와 함께 결과 반환
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<ReviewDetailResponseDto> findReviewByRestaurantId(UUID restaurantId, ReviewSearchCondition condition, Pageable pageable) {
        // QReview, QMenu, QRestaurant, QOrder 객체 생성 (이전에 정의되어야 함)
        QReview review = QReview.review;
        QMenu menu = QMenu.menu;
        QRestaurant restaurant = QRestaurant.restaurant;
        QOrder order = QOrder.order;

        // 기본적으로 페이지 크기 10으로 고정, condition에서 크기가 지정되면 변경
        int pageSize = pageable.getPageSize() > 0 ? pageable.getPageSize() : 10;

        // Review 조회 쿼리
        List<ReviewDetailResponseDto> content = queryFactory
                .select(Projections.constructor(ReviewDetailResponseDto.class,
                        review.id,
                        order.id,
                        menu.id,
                        restaurant.id,
                        review.title,
                        review.content,
                        review.rating,
                        menu.name,
                        restaurant.name,
                        review.createdBy,
                        review.createdAt
                ))
                .from(review)
                .join(review.order, order)
                .join(order.menu, menu)
                .join(menu.restaurant, restaurant)
                .where(
                        review.isDeleted.isFalse(),  // 삭제되지 않은 리뷰만 필터링
                        restaurant.id.eq(restaurantId),  // 특정 레스토랑의 리뷰만 필터링
                        keywordContains(condition.keyword()),  // 제목, 내용, 작성자에서 검색
                        createdAtBetween(condition.startDate(), condition.endDate())  // 날짜 범위 필터링
                )
                .orderBy(
                        condition.isAsc() ? review.createdAt.asc() : review.createdAt.desc(),  // 기본 정렬: 생성일 기준
                        condition.isAsc() ? review.updatedAt.asc() : review.updatedAt.desc()   // 추가 정렬: 수정일 기준
                )
                .offset(pageable.getOffset())  // 페이지 오프셋 적용
                .limit(pageSize)  // 페이지 크기 제한
                .fetch();  // 결과를 리스트로 가져옴

        // 총 레코드 수를 구하는 쿼리 (페이징에 필요)
        Long total = queryFactory
                .select(review.count())
                .from(review)
                .join(review.order, order)
                .join(order.menu, menu)
                .join(menu.restaurant, restaurant)
                .where(
                        review.isDeleted.isFalse(),
                        restaurant.id.eq(restaurantId),  // 특정 레스토랑의 리뷰만 필터링
                        keywordContains(condition.keyword()),
                        createdAtBetween(condition.startDate(), condition.endDate())
                )
                .fetchOne();

        // 총 레코드 수가 null일 경우 0으로 처리
        total = total != null ? total : 0;

        // PageImpl을 사용하여 페이징 정보와 함께 결과 반환
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Optional<Review> findReviewById(UUID reviewId, String userId) {
        QReview review = QReview.review;

        // reviewId와 userId를 기반으로 리뷰를 찾는 쿼리
        Review foundReview = queryFactory
                .selectFrom(review)
                .where(
                        review.id.eq(reviewId),       // 리뷰 ID 조건
                        review.createdBy.eq(userId),   // 작성자(userId) 조건
                        review.isDeleted.isFalse()    // 삭제되지 않은 리뷰만
                )
                .fetchOne();  // 결과가 하나만 있을 경우 fetchOne()

        return Optional.ofNullable(foundReview);  // 결과가 null이면 Optional.empty() 반환
    }

    // 검색 조건을 처리하는 메서드 (조건에 맞는 부분을 반환)
    private BooleanExpression keywordContains(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return review.title.containsIgnoreCase(keyword)
                    .or(review.content.containsIgnoreCase(keyword))
                    .or(review.createdBy.containsIgnoreCase(keyword));  // 제목, 내용, 작성자에서 검색
        }
        return null;  // 검색어가 없으면 조건 없음
    }

    // 날짜 범위를 처리하는 메서드 (날짜 조건이 있을 때만 적용)
    private BooleanExpression createdAtBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return review.createdAt.between(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
        }
        return null;
    }
}

