package com.sashimi.recommendation.presentation.api.response;

import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.domain.model.RecommendationAnalysisStatus;

import java.time.LocalDateTime;

public record JobPostingRecommendationResponse (
        Long recommendationId,
        String jobTitle,
        RecommendationAnalysisStatus analysisStatus,
        boolean resumeBased,
        Integer matchRate,
        LocalDateTime createdAt
) {
    public static JobPostingRecommendationResponse from(JobPostingRecommendation recommendation) {
        return new JobPostingRecommendationResponse(
                recommendation.recommendationId(),
                recommendation.jobTitle(),
                recommendation.analysisStatus(),
                recommendation.resumeBased(),
                recommendation.matchRate(),
                recommendation.createdAt()
        );
    }
}
