package com.ana29.deliverymanagement.user.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SigninRequestDto {
    private String id;
    private String password;
}