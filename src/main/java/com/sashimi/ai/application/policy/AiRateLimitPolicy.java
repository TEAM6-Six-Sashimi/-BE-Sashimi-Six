package com.sashimi.ai.application.policy;

import com.sashimi.ai.domain.model.AiFeatureType;
import com.sashimi.ai.infrastructure.persistence.SpringDataAiRequestHistoryRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AiRateLimitPolicy {

    private static final int FEATURE_HOURLY_LIMIT = 10;
    private static final int TOTAL_HOURLY_LIMIT = 30;

    private final SpringDataAiRequestHistoryRepository repository;

    public AiRateLimitPolicy(
            SpringDataAiRequestHistoryRepository repository
    ) {
        this.repository = repository;
    }

    public void validate(
            Long userId,
            AiFeatureType featureType
    ) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        long featureRequestCount =
                repository.countByUserIdAndFeatureTypeAndCreatedAtGreaterThanEqual(
                        userId,
                        featureType,
                        oneHourAgo
                );

        if (featureRequestCount >= FEATURE_HOURLY_LIMIT) {
            throw new BusinessException(ErrorCode.AI_RATE_LIMIT_EXCEEDED);
        }

        long totalRequestCount =
                repository.countByUserIdAndCreatedAtGreaterThanEqual(
                        userId,
                        oneHourAgo
                );

        if (totalRequestCount >= TOTAL_HOURLY_LIMIT) {
            throw new BusinessException(ErrorCode.AI_RATE_LIMIT_EXCEEDED);
        }
    }
}