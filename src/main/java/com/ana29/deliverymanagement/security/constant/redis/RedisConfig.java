package com.ana29.deliverymanagement.security.constant.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisConfig {
    SECURITY_CONTEXT_KEY_PREFIX("UserCacheStore::"),
    EXPIRATION_TIME(Long.toString(60 * 60 * 1000L)); // 60분 , 사용시 Long 변환

    private final String getRedisConfig;
}
