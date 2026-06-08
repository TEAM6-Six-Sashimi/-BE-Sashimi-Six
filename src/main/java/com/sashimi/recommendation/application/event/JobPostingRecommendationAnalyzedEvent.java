package com.sashimi.recommendation.application.event;

import java.time.LocalDateTime;

public record JobPostingRecommendationAnalyzedEvent (
        Long userId,
        Long recommendationId,
        String jobTitle,
        Integer matchRate,
        LocalDateTime analyzedAt
) {
}
