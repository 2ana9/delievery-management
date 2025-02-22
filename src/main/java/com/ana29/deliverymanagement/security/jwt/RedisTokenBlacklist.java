package com.ana29.deliverymanagement.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisTokenBlacklist {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 토큰을 블랙리스트에 추가하고 TTL(만료시간)을 설정합니다.
     * 예를 들어, tokenExpirationMillis 값은 토큰의 남은 유효 시간(밀리초)일 수 있습니다.
     */
    public void addToken(String token, long tokenExpirationMillis) {
        // token 키를 "blacklisted" 값과 함께 저장하고, TTL을 설정합니다.
        redisTemplate.opsForValue().set(token, "blacklisted", tokenExpirationMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * 주어진 토큰이 블랙리스트에 있는지 확인합니다.
     */
    public boolean isTokenBlacklisted(String token) {
        Boolean exists = redisTemplate.hasKey(token);
        return exists != null && exists;
    }

    /**
     * 블랙리스트에서 특정 토큰을 제거합니다.
     */
    public void removeToken(String token) {
        redisTemplate.delete(token);
    }

    /**
     * 디버깅용 - 블랙리스트에 저장된 모든 토큰을 조회합니다.
     * (운영환경에서는 keys() 사용 시 성능에 주의해야 합니다.)
     */
    public Object getBlacklistedTokens() {
        return redisTemplate.keys("*");
    }
}
