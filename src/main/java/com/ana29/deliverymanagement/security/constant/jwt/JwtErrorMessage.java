package com.ana29.deliverymanagement.security.constant.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtErrorMessage {
    Invalid("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다."),
    ExpiredAndUnsupported("Expired JWT token, 만료된 JWT token 입니다."),
//    Unsupported("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다."),
    Empty("JWT claims is empty, 잘못된 JWT 토큰 입니다."),
    Error("Wrong JWT Tokenm, 잘못된 JWT 토큰 입니다.");

    private final String getJwtErrorMessage;

}
