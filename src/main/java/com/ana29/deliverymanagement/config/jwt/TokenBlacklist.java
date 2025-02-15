package com.ana29.deliverymanagement.config.jwt;

import com.ana29.deliverymanagement.constant.jwt.JwtConfigEnum;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class TokenBlacklist {
    // 토큰과 만료 시간을 저장하는 thread-safe한 Map
    public static final Map<String, Long> BLACKLIST = new ConcurrentHashMap<>();

    // 외부에서 인스턴스 생성을 막기 위한 private 생성자
    private TokenBlacklist() {}

    // 토큰을 블랙리스트에 추가하는 메소드 (현재 시간 + 유지시간으로 설정)
    public static void addToken(String token) {
        long expirationTime = System.currentTimeMillis()
                + Long.parseLong(JwtConfigEnum.BLACK_TOKEN_TIME.getGetJwtConfig().trim());
        BLACKLIST.put(token, expirationTime);
    }

    // 토큰이 블랙리스트에 있는지 확인하는 메소드
    public static boolean isTokenBlacklisted(String token) {
        return BLACKLIST.containsKey(token);
    }

    // 블랙리스트에서 특정 토큰을 제거하는 메소드 (필요 시 사용)
    public static void removeToken(String token) {
        BLACKLIST.remove(token);
    }

    // 블랙리스트에서 현재 저장된 토큰 목록 반환 (디버깅용)
    public static Map<String, Long> getBlacklistedTokens() {
        return new ConcurrentHashMap<>(BLACKLIST);
    }

    // 블랙리스트에서 만료된 토큰 제거
    public void cleanExpiredTokens(long currentTime) {
        BLACKLIST.entrySet().removeIf(entry -> entry.getValue() < currentTime);
    }
}
