package com.sashimi.recommendation.presentation.api.response;

import com.sashimi.recommendation.domain.model.JobPostingRecommendation;

public record LatestJobPostingRecommendationResponse(
        JobPostingRecommendationResponse recommendation
) {
    public static LatestJobPostingRecommendationResponse of(
            JobPostingRecommendation recommendation
    ) {
        return new LatestJobPostingRecommendationResponse(
                JobPostingRecommendationResponse.from(recommendation)
        );
    }

    public static LatestJobPostingRecommendationResponse empty() {
        return new LatestJobPostingRecommendationResponse(null);
    }
}