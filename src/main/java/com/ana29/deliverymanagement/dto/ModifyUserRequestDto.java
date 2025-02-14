package com.ana29.deliverymanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyUserRequestDto {

    @NotBlank
    private String nickname;
    @NotBlank
    private String phone;

}
