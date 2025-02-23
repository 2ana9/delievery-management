package com.ana29.deliverymanagement.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank
    private String id;

    // 닉네임 : 알파벳 소문자와 숫자가 둘 모두 반드시 포함, 알파벳 소문자와 숫자로만 구성, 4-10자, 공백허용 x
    // 비밀번호 : 알파벳 대소문자, 숫자, 특수문자 셋 모두 반드시 포함, 8-15자, 공백허용 x
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[0-9])[a-z0-9^\\s]{4,10}$",
            message = "닉네임은 알파벳 소문자와 숫자로 이루어진 4자 이상 10자 이하로 입력해주세요.")
    private String nickname;
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d\\s])[^\\s]{8,15}$",
            message = "비밀번호는 알파벳 대소문자, 숫자, 특수문자를 포함한 8자 이상 15자 이하입니다.")
    private String password;

    @Email(message = "유효한 이메일 주소를 입력하세요.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,25}$", message = "유효한 이메일 주소를 입력하세요.")
    private String email;

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    @Pattern(regexp = "^010-?\\d{4}-?\\d{4}$", message = "유효한 전화번호를 입력하세요.")
    private String phone;

    private String tokenValue;
}