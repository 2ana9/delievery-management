package com.ana29.deliverymanagement.security.constant.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisErrorMessage {
    NOTINCONTEXT("⚠️ SecurityContext에 UserDetails가 존재하지 않습니다."),
    // 사용자 권한 값의 KEY
    SAVEUSERINREDIS("✅ UserDetails가 Redis에 저장되었습니다: {}"),
    // Token 식별자
    CANTFINDUSERINREDIS("⚠️ Redis에서 UserDetails를 찾을 수 없습니다. username: {}"),
    // Token 식별자 제거
    BEARER_PREFIX_COUNT(Integer.toString(7)),
    // 발급 토큰 만료시간
    EXPIRATION_TIME(Long.toString(60 * 60 * 1000L)), // 60분 , 사용시 Long 변환
    // 블랙리스트 토큰 유지 시간
    BLACK_TOKEN_TIME(Long.toString(60 * 10 * 1000L));

    private final String getJwtConfig;
}
