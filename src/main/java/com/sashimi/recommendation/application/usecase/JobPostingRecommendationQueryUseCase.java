package com.sashimi.recommendation.application.usecase;

import com.sashimi.recommendation.domain.model.JobPostingRecommendation;

public interface JobPostingRecommendationQueryUseCase {

    JobPostingRecommendation getById(Long userId, Long recommendationId);
}