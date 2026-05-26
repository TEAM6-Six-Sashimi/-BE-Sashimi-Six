package com.sashimi.recommendation.presentation.api.request;

import com.sashimi.recommendation.domain.model.RecommendationInputType;

public record CreateJobPostingRecommendationRequest (
        RecommendationInputType inputType,
        String sourceUrl,
        String rawContent
) {
}
