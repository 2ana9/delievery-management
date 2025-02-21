package com.ana29.deliverymanagement.security.service;

import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityContextRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper; // 🔹 CacheConfig에서 주입받음

    private static final String SECURITY_CONTEXT_KEY_PREFIX = "SECURITY_CONTEXT:";
    private static final long EXPIRATION_TIME = 60; // 만료 시간 (분 단위)

    /**
     * 🔹 SecurityContext의 UserDetails를 Redis에 저장
     */
    public void saveUserDetailsToRedis() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null || !(context.getAuthentication().getPrincipal() instanceof UserDetails userDetails)) {
            log.warn("⚠️ SecurityContext에 UserDetails가 존재하지 않습니다.");
            return;
        }

        String key = SECURITY_CONTEXT_KEY_PREFIX + userDetails.getUsername();
        try {
            // ✅ Redis에 UserDetails 객체를 JSON으로 저장
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(userDetails), EXPIRATION_TIME, TimeUnit.MINUTES);
            log.info("✅ UserDetails가 Redis에 저장되었습니다: {}", key);
        } catch (Exception e) {
            log.error("❌ UserDetails 저장 중 오류 발생", e);
        }
    }

    /**
     * 🔹 Redis에서 UserDetails 불러오기
     */
    public UserDetails loadUserDetailsFromRedis(String username) {
        String key = SECURITY_CONTEXT_KEY_PREFIX + username;
        String userDetailsJson = (String) redisTemplate.opsForValue().get(key);

        if (userDetailsJson == null) {
            log.warn("⚠️ Redis에서 UserDetails를 찾을 수 없습니다. username: {}", username);
            return null;
        }

        try {
            // ✅ JSON을 UserDetails 객체로 변환하여 반환
            return objectMapper.readValue(userDetailsJson, UserDetailsImpl.class);
        } catch (Exception e) {
            log.error("❌ UserDetails 역직렬화 실패", e);
            return null;
        }
    }

    /**
     * 🔹 Redis에서 SecurityContext 삭제 (로그아웃 시 사용)
     */
    public void removeUserDetailsFromRedis(String username) {
        String key = SECURITY_CONTEXT_KEY_PREFIX + username;
        redisTemplate.delete(key);
        log.info("✅ Redis에서 UserDetails가 삭제되었습니다. username: {}", username);
    }
}
