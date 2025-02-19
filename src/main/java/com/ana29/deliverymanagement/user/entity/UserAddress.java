package com.ana29.deliverymanagement.user.entity;

import com.ana29.deliverymanagement.global.entity.Timestamped;
import com.ana29.deliverymanagement.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name ="p_useraddress")
public class UserAddress extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "useraddress_id", columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    @Builder.Default
    private Boolean currentAddress = false;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
