package com.ana29.deliverymanagement.repository;

import com.ana29.deliverymanagement.constant.OrderStatusEnum;
import com.ana29.deliverymanagement.entity.Order;
import com.ana29.deliverymanagement.entity.QOrder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	QOrder order = QOrder.order;

	@Override
	public Optional<Order> findLatestPendingOrder(String userId) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(order)  // user join 제거
				.where(
					order.user.Id.eq(userId),
					order.orderStatus.eq(OrderStatusEnum.PENDING)
				)
				.orderBy(order.createdAt.desc())
				.limit(1)
				.fetchOne()
		);
	}
}
