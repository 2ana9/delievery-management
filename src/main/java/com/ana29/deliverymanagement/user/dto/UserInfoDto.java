package com.ana29.deliverymanagement.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDto {
    private String id;
    private String nickname;
    private String email;
    private String phone;
    boolean isAdmin;
}