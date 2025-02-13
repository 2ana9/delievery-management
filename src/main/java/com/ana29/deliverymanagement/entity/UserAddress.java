package com.ana29.deliverymanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name ="p_useraddress")
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "useraddres_id", columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String address;

    @CreatedDate
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    private LocalDateTime deleted_at;

    @Column(nullable = false)
    private String created_by;

    private String updated_by;

    private String deleted_by;

    //외래키의경우 엔티티 작성완료 후 추가예정


}
