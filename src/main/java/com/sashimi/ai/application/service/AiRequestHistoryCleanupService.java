package com.sashimi.ai.application.service;

import com.sashimi.ai.infrastructure.persistence.SpringDataAiRequestHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AiRequestHistoryCleanupService {

    private static final long RETENTION_DAYS = 7;

    private final SpringDataAiRequestHistoryRepository
            aiRequestHistoryRepository;

    public AiRequestHistoryCleanupService(
            SpringDataAiRequestHistoryRepository aiRequestHistoryRepository
    ) {
        this.aiRequestHistoryRepository =
                aiRequestHistoryRepository;
    }

    @Transactional
    public long deleteExpiredHistories() {
        LocalDateTime threshold =
                LocalDateTime.now()
                        .minusDays(RETENTION_DAYS);

        long deletedCount =
                aiRequestHistoryRepository.deleteByCreatedAtBefore(
                        threshold
                );

        log.info(
                "만료된 AI 요청 이력 삭제 완료: retentionDays={}, threshold={}, deletedCount={}",
                RETENTION_DAYS,
                threshold,
                deletedCount
        );

        return deletedCount;
    }
}