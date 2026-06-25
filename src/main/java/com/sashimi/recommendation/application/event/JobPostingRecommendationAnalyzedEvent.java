package com.sashimi.recommendation.application.event;

import java.time.LocalDateTime;

public record JobPostingRecommendationAnalyzedEvent (
        Long userId,
        Long recommendationId,
        String jobRole,
        LocalDateTime analyzedAt
) {
}
