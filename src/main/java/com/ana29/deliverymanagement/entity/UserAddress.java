package com.ana29.deliverymanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name ="p_useraddress")
public class UserAddress extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
