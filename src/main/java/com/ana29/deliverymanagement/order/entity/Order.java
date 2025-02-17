package com.ana29.deliverymanagement.order.entity;

import com.ana29.deliverymanagement.global.constant.OrderStatusEnum;
import com.ana29.deliverymanagement.global.entity.Timestamped;
import com.ana29.deliverymanagement.restaurant.entity.Menu;
import com.ana29.deliverymanagement.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더를 통한 생성만 허용
@Builder
@Table(name = "p_order")
public class Order extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "order_id", columnDefinition = "uuid")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_id", nullable = false)
	private Menu menu;

	@Column(nullable = false)
	private Long totalPrice;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private OrderStatusEnum orderStatus = OrderStatusEnum.PENDING;

	@Column(nullable = false)
	private Integer quantity;

	@Column(length = 100)
	private String orderRequest;

	@Column(nullable = false)
	@Builder.Default
	private boolean isDeleted = false;

	@OneToOne(mappedBy = "order")
	private Payment payment;

	public void updateStatus(OrderStatusEnum newStatus) {
		if(!this.orderStatus.canChangeTo(newStatus)){
			throw new RuntimeException("Order status cannot be changed");
		}
		this.orderStatus = newStatus;
	}

	public void delete(String deletedBy){
		super.delete(deletedBy);
		this.isDeleted = true;
	}

	public boolean isOwner(String userId){
		return this.user.getId().equals(userId);
	}
}
