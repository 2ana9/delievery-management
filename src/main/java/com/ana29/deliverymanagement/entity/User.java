package com.ana29.deliverymanagement.entity;

import com.ana29.deliverymanagement.constant.UserRoleEnum;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더를 통한 생성만 허용
@Builder
@Table(name = "p_users")
public class User extends Timestamped {
    @Id
    @Column(length = 10, nullable = false)
    private String Id; // 유저 ID (Primary Key)

    @Column(length = 50, nullable = false)
    private String nickname; // 닉네임

    @Column(length = 50, nullable = false)
    private String email; // 이메일 (유니크 제약 조건 추가)

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(length = 20, nullable = false)
    private String phone; // 연락처

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(length = 255, nullable = false)
    private String currentAddress; // 대표 주소


}
