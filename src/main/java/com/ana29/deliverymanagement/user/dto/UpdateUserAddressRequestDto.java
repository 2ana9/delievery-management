package com.ana29.deliverymanagement.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record UpdateUserAddressRequestDto(
        @NotNull String address,
        @Pattern(regexp = "^.{0,50}$", message = "상세 주소는 최대 50자까지 입력할 수 있습니다.")
        String detail
) {}
