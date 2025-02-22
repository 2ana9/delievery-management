package com.ana29.deliverymanagement.security.constant.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtConfigEnum {
    // Header KEY 값
    AUTHORIZATION_HEADER("Authorization"),
    // 사용자 권한 값의 KEY
    AUTHORIZATION_KEY("auth"),
    // Token 식별자
    BEARER_PREFIX("Bearer "),
    // Token 식별자 제거
    BEARER_PREFIX_COUNT(Integer.toString(7)),
    // 발급 토큰 만료시간
    TOKEN_TIME(Long.toString(15 * 60 * 1000L)); // 15분 , 사용시 Long 변환

    private final String getJwtConfig;
}
