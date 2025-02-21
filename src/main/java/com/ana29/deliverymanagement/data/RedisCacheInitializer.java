package com.ana29.deliverymanagement.data;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheInitializer {

    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void clearRedisCache() {
        // 현재 선택된 DB의 모든 데이터를 삭제 (flushDb)
        redisTemplate.getConnectionFactory().getConnection().flushDb();
        // 전체 Redis 서버의 모든 DB 데이터를 삭제하려면 flushAll()을 사용
        // redisTemplate.getConnectionFactory().getConnection().flushAll();
        log.info("Redis 저장소가 초기화되었습니다.");
    }
}
