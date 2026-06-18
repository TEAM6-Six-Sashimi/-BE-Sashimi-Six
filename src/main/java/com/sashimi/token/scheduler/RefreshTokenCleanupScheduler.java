package com.sashimi.token.scheduler;

import com.sashimi.token.service.RefreshService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

    private final RefreshService refreshService;

    @Scheduled(cron = "0 0 3 * * *")
    public void deleteExpiredTokens() {
        log.info("[RefreshTokenCleanup] 만료된 refresh token 정리 시작");
        refreshService.deleteExpiredTokens();
        log.info("[RefreshTokenCleanup] 만료된 refresh token 정리 완료");
    }
}
