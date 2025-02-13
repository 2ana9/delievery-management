package com.ana29.deliverymanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_area_codes")
@Getter
@NoArgsConstructor
public class AreaCode extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "area_code_id", columnDefinition = "uuid")
	private UUID id;

	@Column(length = 10, nullable = false)
	private String code;
//주석추가
	@Column(nullable = false)
	private String cityName;
}
