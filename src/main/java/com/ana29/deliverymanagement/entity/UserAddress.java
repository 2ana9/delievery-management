package com.ana29.deliverymanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더를 통한 생성만 허용
@Builder
@Table(name ="p_useraddress")
public class UserAddress extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "useraddress_id", columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false) //length default 255여서 추가 x
    private String address;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

//    @OneToOne
//    @JoinColumn(name = "area_id")
//    private Area area;


}
