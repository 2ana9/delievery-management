package com.ana29.deliverymanagement.data;

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
        // 전체 키 조회 (클러스터 환경에서는 주의 필요)
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            System.out.println("Redis 저장소가 초기화되었습니다.");
        } else {
            System.out.println("Redis에 삭제할 키가 없습니다.");
        }
    }
}
