package com.ana29.deliverymanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;

@Getter
@Setter
public class UpdateRequestDto {
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String nickname;

    @Email(message = "유효한 이메일 주소를 입력하세요.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    private String phone;

    // 선택적으로 추가 필드
    private String currentAddress;
}
