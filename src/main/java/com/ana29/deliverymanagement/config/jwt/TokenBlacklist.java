package com.ana29.deliverymanagement.config.jwt;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class TokenBlacklist {
    // 스태틱으로 관리되는 블랙리스트 컬렉션 (thread-safe)
    private static final Set<String> BLACKLIST = ConcurrentHashMap.newKeySet();

    // 외부에서 인스턴스 생성을 막기 위한 private 생성자
    private TokenBlacklist() {
    }

    // 토큰을 블랙리스트에 추가하는 메소드
    public static void addToken(String token) {
        BLACKLIST.add(token);
    }

    // 토큰이 블랙리스트에 있는지 확인하는 메소드
    public static boolean isTokenBlacklisted(String token) {
        return BLACKLIST.contains(token);
    }

    // 블랙리스트에 있는 토큰을 제거하는 메소드 (필요 시)
    public static void removeToken(String token) {
        BLACKLIST.remove(token);
    }

    // 블랙리스트에 포함된 모든 토큰을 읽기 전용 Set으로 반환
    public static Set<String> getBlacklistedTokens() {
        return Collections.unmodifiableSet(BLACKLIST);
    }

}
