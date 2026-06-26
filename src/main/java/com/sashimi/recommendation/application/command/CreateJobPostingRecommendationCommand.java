package com.sashimi.recommendation.application.command;

import com.sashimi.recommendation.domain.model.RecommendationInputType;

public record CreateJobPostingRecommendationCommand(
        Long userId,
        Long resumeId,
        RecommendationInputType inputType,
        String sourceUrl,
        String rawContent
) {
}