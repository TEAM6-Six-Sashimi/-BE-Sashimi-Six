package com.sashimi.ai.application.scheduler;

import com.sashimi.ai.application.service.AiRequestHistoryCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AiRequestHistoryCleanupScheduler {

    private final AiRequestHistoryCleanupService
            aiRequestHistoryCleanupService;

    public AiRequestHistoryCleanupScheduler(
            AiRequestHistoryCleanupService aiRequestHistoryCleanupService
    ) {
        this.aiRequestHistoryCleanupService =
                aiRequestHistoryCleanupService;
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void deleteExpiredAiRequestHistories() {
        log.info("만료된 AI 요청 이력 삭제 스케줄러 시작");

        long deletedCount =
                aiRequestHistoryCleanupService
                        .deleteExpiredHistories();

        log.info(
                "만료된 AI 요청 이력 삭제 스케줄러 종료: deletedCount={}",
                deletedCount
        );
    }
}