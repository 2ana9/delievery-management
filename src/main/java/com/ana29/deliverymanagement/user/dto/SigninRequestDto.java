package com.ana29.deliverymanagement.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SigninRequestDto {
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[0-9])[a-z0-9^\\s]{4,10}$",
            message = "닉네임은 알파벳 소문자와 숫자로 이루어진 4자 이상 10자 이하로 입력해주세요.")
    private String id;
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d\\s])[^\\s]{8,15}$",
            message = "비밀번호는 알파벳 대소문자, 숫자, 특수문자를 포함한 8자 이상 15자 이하입니다.")
    private String password;
}