package com.sashimi.recommendation.application.command;

import com.sashimi.recommendation.domain.model.RecommendationInputType;

public record CreateJobPostingRecommendationCommand (
        Long userId,
        RecommendationInputType inputType,
        String sourceUrl,
        String rawContent
) {
}
