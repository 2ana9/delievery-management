package com.ana29.deliverymanagement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name ="p_useraddress")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAddress extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) //그대로 작성하기!
    @Column(name = "useraddress_id", columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false) //length default 255여서 추가 x
    private String address;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

//    @OneToOne
//    @JoinColumn(name = "area_id")
//    private Area area;


}
