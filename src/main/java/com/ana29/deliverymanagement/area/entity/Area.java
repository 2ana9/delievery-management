package com.ana29.deliverymanagement.area.entity;

import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "p_area")
public class Area {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "area_id", columnDefinition = "uuid")
	private UUID id;

	@Column(length = 20, nullable = false)
	private String city;	//예) 서울특별시

	@Column(length = 20, nullable = false)
	private String district;	//예) 종로구

	//지번 주소 (법정동)
	@Column(length = 50, nullable = false)
	private String town;	//예) 청운동

	@Column(length = 10, nullable = false)
	private String lot_main_no;	//지번 본번 예) (50)

	@Column(length = 10, nullable = false)
	private String lot_sub_no;	//지번 부번 예) (6)

	//도로명 주소
	@Column(length = 50, nullable = false)
	private String road_name;	//자하문로

	@Column(length = 10, nullable = false)
	private String building_main_no;	//건물 본번 예) (115)

	@Column(length = 10, nullable = false)
	private String building_sub_no;	//건물 부번 예) (14)

	@OneToMany(mappedBy = "area")
	private List<Restaurant> restaurantList = new ArrayList<>();

}
