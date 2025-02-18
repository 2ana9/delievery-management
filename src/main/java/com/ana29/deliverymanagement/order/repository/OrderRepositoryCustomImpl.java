package com.ana29.deliverymanagement.order.repository;

import static com.ana29.deliverymanagement.restaurant.entity.QMenu.menu;
import static com.ana29.deliverymanagement.restaurant.entity.QRestaurant.restaurant;

import com.ana29.deliverymanagement.global.constant.OrderStatusEnum;
import com.ana29.deliverymanagement.order.dto.OrderHistoryResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderSearchCondition;
import com.ana29.deliverymanagement.order.entity.Order;
import com.ana29.deliverymanagement.order.entity.QOrder;
import com.ana29.deliverymanagement.order.entity.QPayment;
import com.ana29.deliverymanagement.user.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	QOrder order = QOrder.order;

	@Override
	public Page<OrderHistoryResponseDto> findOrderHistory(String userId,
		OrderSearchCondition condition, Pageable pageable) {

		List<OrderHistoryResponseDto> content = queryFactory
			.select(Projections.constructor(OrderHistoryResponseDto.class,
				order.id,
				restaurant.name,
				menu.name,
				order.orderStatus,
				order.createdAt))
			.from(order)
			.join(order.menu, menu)
			.join(menu.restaurant, restaurant)
			.where(
				order.user.Id.eq(userId),
				order.isDeleted.isFalse(),
				order.orderStatus.ne(OrderStatusEnum.PENDING),
				keywordContains(condition.keyword()),
				statusIn(condition.statuses()),
				createdAtBetween(condition.startDate(), condition.endDate()))
			.orderBy(condition.isAsc() ? order.createdAt.asc() : order.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long fetchedCount = queryFactory
			.select(order.count())
			.from(order)
			.join(order.menu, menu)
			.join(menu.restaurant, restaurant)
			.where(
				order.user.Id.eq(userId),
				order.isDeleted.isFalse(),
				order.orderStatus.ne(OrderStatusEnum.PENDING),
				keywordContains(condition.keyword()),
				statusIn(condition.statuses()),
				createdAtBetween(condition.startDate(), condition.endDate())
			)
			.fetchOne();

		long total = fetchedCount != null ? fetchedCount : 0;

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public Optional<Order> findOrderById(UUID orderId, String userId) {
		QPayment payment = QPayment.payment;
		QUser user = QUser.user;

		Order result = queryFactory
			.selectFrom(order)
			.leftJoin(order.user, user).fetchJoin()
			.leftJoin(order.menu, menu).fetchJoin()
			.leftJoin(menu.restaurant, restaurant).fetchJoin()
			.leftJoin(payment).on(payment.order.id.eq(order.id)).fetchJoin()
			.where(
				order.id.eq(orderId)
					.and(order.user.Id.eq(userId))
			)
			.fetchOne();

		return Optional.ofNullable(result);
	}


	private BooleanExpression keywordContains(String keyword) {
		return StringUtils.hasText(keyword) ? menu.name.containsIgnoreCase(keyword)
			.or(restaurant.name.containsIgnoreCase(keyword)) : null;
	}

	private BooleanExpression statusIn(List<OrderStatusEnum> statuses) {
		return statuses != null && !statuses.isEmpty() ? order.orderStatus.in(statuses) : null;
	}

	private BooleanExpression createdAtBetween(LocalDate startDate, LocalDate endDate) {
		if (startDate == null && endDate == null) {
			return null;
		}
		if (startDate == null) {
			return order.createdAt.loe(endDate.atTime(23, 59, 59));
		}
		if (endDate == null) {
			return order.createdAt.goe(startDate.atTime(0, 0, 0));
		}

		return order.createdAt.between(startDate.atTime(0, 0, 0), endDate.atTime(23, 59, 59));
	}
}
