package com.ana29.deliverymanagement.user.dto;

import com.ana29.deliverymanagement.user.constant.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDto {
    private String id;
    private String nickname;
    private String email;
    private String phone;
    UserRoleEnum isAdmin;
}