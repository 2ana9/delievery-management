package com.ana29.deliverymanagement.entity;

import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
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
public class Review extends Timestamped  {

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

	@JsonIgnore  // 이 필드는 JSON으로 직렬화되지 않음
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@JsonIgnore  // 이 필드는 JSON으로 직렬화되지 않음
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@JsonIgnore  // 이 필드는 JSON으로 직렬화되지 않음
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restaurant_id", nullable = false)
	private Restaurant restaurant;
}
