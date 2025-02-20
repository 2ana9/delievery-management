package com.ana29.deliverymanagement.area.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "p_area")
public class Area {
// 데이터를 직접 입력시 uuid 생성 안됨
//	@Id
//	@GeneratedValue(strategy = GenerationType.UUID)
//	@Column(name = "area_id", columnDefinition = "uuid")
//	private UUID id;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 자동 증가 ID 관리
	@Column(name = "area_id", updatable = false, nullable = false)
	private Long id;

	@Column(length = 20, nullable = false)
	private String city;	//예) 서울특별시

	@Column(length = 20)		//세종특별시는 district 가 없는 경우가 있음
	private String district;	//예) 종로구

	//지번 주소 (법정동)
	@Column(length = 50, nullable = false)
	private String town;	//예) 청운동

	@Column(length = 20)
	private String village;  // 예) 서리

	@Column(length = 10)
	private String townMainNo;	//지번 본번 예) (50)

	@Column(length = 10)
	private String townSubNo;	//지번 부번 예) (6)

	//도로명 주소
	@Column(length = 50)
	private String road;	//자하문로

	@Column(length = 10)
	private String roadMainNo;	//도로명 본번 예) (115)

	@Column(length = 10)
	private String roadSubNo;	//도로명 부번 예) (14)

	@Column(length = 50)
	private String buildingName;  // 건물명 (예: 삼성타워, 현대아파트)

	@Column(length = 10, nullable = false)
	private String legalCode; // 5자리 법정동 코드 (예: 11110) 구단위로 필터링 하기 위해 5자리로 고정

	@Column
	private String roadAddress; // 지번 주소 (예: 서울특별시 종로구 청운동 50-6)

	@Column
	private String jibunAddress; // 도로명 주소 (예: 서울특별시 종로구 자하문로 115-14)
}
