package com.ana29.deliverymanagement.constant.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtConfigEnum {
    AUTHORIZATION_HEADER("Authorization"),
    AUTHORIZATION_KEY("auth"),
    BEARER_PREFIX("Bearer "),
    TOKEN_TIME(Long.toString(60 * 60 * 1000L)); // 60분 , 사용시 Long 변환

    private final String getJwtConfig;
}
