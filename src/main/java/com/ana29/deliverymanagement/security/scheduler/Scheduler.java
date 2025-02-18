package com.ana29.deliverymanagement.security.scheduler;

import com.ana29.deliverymanagement.security.jwt.TokenBlacklist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j(topic = "Scheduler")
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final TokenBlacklist tokenBlacklist;

    // 초, 분, 시, 일, 월, 주 순서
    /**
     * 🔹 블랙리스트에서 만료된 토큰 자동 제거
     * - 1분마다 실행 (cron = "0 * * * * *")
     * - cron = "0 0 1 * * *" → 매일 새벽 1시 실행하려면 이렇게 변경
     */
    @Scheduled(cron = "0 * * * * *") // 1분마다 실행
    public void removeExpiredBlackTokens() {
        long currentTime = System.currentTimeMillis();
        int beforeSize = TokenBlacklist.getBlacklistedTokens().size();

        tokenBlacklist.cleanExpiredTokens(currentTime);

        int afterSize = TokenBlacklist.getBlacklistedTokens().size();
        log.info("만료된 토큰 제거 완료 (삭제 전: {}, 삭제 후: {})", beforeSize, afterSize);
    }

}