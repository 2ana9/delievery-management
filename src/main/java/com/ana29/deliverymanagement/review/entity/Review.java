package com.ana29.deliverymanagement.review.entity;

import com.ana29.deliverymanagement.order.entity.Order;
import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import com.ana29.deliverymanagement.global.entity.Timestamped;
import com.ana29.deliverymanagement.review.dto.ReviewRequestDto;
import com.ana29.deliverymanagement.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.UUID;

import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더를 통한 생성만 허용
@Builder
@Table(name = "p_reviews")
public class Review extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "review_id", columnDefinition = "uuid")
	private UUID id;

	@Column(length = 100, nullable = false)
	private String title;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false, columnDefinition = "integer check (rating between 1 and 5)")
	private Integer rating;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restaurant_id", nullable = false)
	private Restaurant restaurant;

	// 기존 엔티티 값을 업데이트하는 메서드
	public void updateReview(String title, String content, Integer rating) {
		this.title = title;
		this.content = content;
		this.rating = rating;
	}
}
