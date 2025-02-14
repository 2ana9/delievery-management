package com.ana29.deliverymanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더를 통한 생성만 허용
@Builder
@Table(name = "p_area")
public class Area extends Timestamped {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "area_id", columnDefinition = "uuid")
	private UUID id;

	@Column(length = 10, nullable = false)
	private String code;

	@Column(nullable = false)
	private String cityName;

	@Column
	private String parentId;
}
