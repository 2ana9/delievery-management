package com.ana29.deliverymanagement.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
public class SignupRequestDto {

    @NotBlank
    private String id;
    @NotBlank
    private String nickname;
    @NotBlank
    private String password;
    @NotBlank
    private String phone;
    @Email
    @NotBlank
    private String email;

    private String currentAddress;

    private boolean admin = false;

    private String adminToken;
}