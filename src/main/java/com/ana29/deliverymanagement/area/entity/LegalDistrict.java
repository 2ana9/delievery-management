package com.ana29.deliverymanagement.area.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "p_legal_district")
public class LegalDistrict {
    @Id
    @Column(name = "legal_code", length = 5)
    private String legalCode; // 5자리 법정동 코드 (PK) 예: 11110) 종로구에 대한 법정동 코드

    @Column(length = 20, nullable = false)
    private String city;    // 예) 서울특별시

    @Column(length = 20)
    private String district;    // 예) 종로구 (세종시는 null일 수 있음)
}