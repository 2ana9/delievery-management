package com.ana29.deliverymanagement.user.entity;

import com.ana29.deliverymanagement.global.entity.Timestamped;
import com.ana29.deliverymanagement.user.constant.UserRoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더를 통한 생성만 허용
@AllArgsConstructor // 빌더를 통한 생성만 허용
@Builder
@Table(name = "p_users")
@JsonIgnoreProperties(ignoreUnknown = true) // ✅ 정의되지 않은 필드는 무시하여 Jackson 역직렬화 오류 방지
public class User extends Timestamped implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @Column(length = 50, nullable = false)
    private String Id; // 유저 ID (Primary Key)

    @Column(length = 50, nullable = false)
    private String nickname; // 닉네임

    @Column(length = 50, nullable = false)
    private String email; // 이메일 (유니크 제약 조건 추가)

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(length = 20, nullable = false)
    private String phone; // 연락처

    public User(String id, String nickname, String email, String password, String phone, UserRoleEnum role) {
        Id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
    }

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

//    @Column(length = 255, nullable = true)
//    private String currentAddress; // 대표 주소

    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAddress> addresses = new ArrayList<>();

    // 주소 추가 메서드
    public void addAddress(UserAddress address) {
        addresses.add(address);
        address.setUser(this);
    }
}
