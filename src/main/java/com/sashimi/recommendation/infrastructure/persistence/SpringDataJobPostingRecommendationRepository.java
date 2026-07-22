package com.sashimi.recommendation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataJobPostingRecommendationRepository
        extends JpaRepository<JobPostingRecommendationJpaEntity, Long> {

    Optional<JobPostingRecommendationJpaEntity> findByRecommendationIdAndUserId(
            Long recommendationId,
            Long userId
    );

    Optional<JobPostingRecommendationJpaEntity> findFirstByUserIdOrderByCreatedAtDesc(
            Long userId
    );
}