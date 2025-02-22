package com.ana29.deliverymanagement.data;

import com.ana29.deliverymanagement.security.constant.redis.RedisConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RedisCacheInitializer {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCacheInitializer(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void clearRedisCache() {
        // 사용자 정보 키의 접두사를 사용 (예: "UserCacheStore::" 또는 properties에 설정한 값)

        // 해당 접두사로 시작하는 키만 조회
        Set<String> keys = redisTemplate.keys(RedisConfig.SECURITY_CONTEXT_KEY_PREFIX.getGetRedisConfig() + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            System.out.println("Redis에서 사용자 정보 키가 초기화되었습니다.");
        } else {
            System.out.println("Redis에 삭제할 사용자 정보 키가 없습니다.");
        }
    }
}
