package com.ana29.deliverymanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

import lombok.*;

@Entity
@Getter
@Setter
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

/*	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restaurant_id")
	private Restaurant restaurant;*/
}
