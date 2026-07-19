package com.sashimi.recommendation.application.usecase;

import com.sashimi.recommendation.domain.model.JobPostingRecommendation;

import java.util.Optional;

public interface JobPostingRecommendationQueryUseCase {

    JobPostingRecommendation getById(Long userId, Long recommendationId);

    Optional<JobPostingRecommendation> getLatest(Long userId);
}