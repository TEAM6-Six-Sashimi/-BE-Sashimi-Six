package com.sashimi.recommendation.domain.repository;

import com.sashimi.recommendation.domain.model.JobPostingRecommendation;

import java.util.Optional;

public interface JobPostingRecommendationRepository {

    JobPostingRecommendation save(JobPostingRecommendation recommendation);

    Optional<JobPostingRecommendation> findLatestByUserId(Long userId);

    Optional<JobPostingRecommendation> findByIdAndUserId(Long recommendationId, Long userId);

}
