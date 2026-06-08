package com.sashimi.recommendation.infrastructure.persistence;

import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.domain.repository.JobPostingRecommendationRepository;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryJobPostingRecommendationRepositoryAdapter
        implements JobPostingRecommendationRepository {

    private final AtomicLong sequence = new AtomicLong(1);
    private final Map<Long, JobPostingRecommendation> recommendations = new HashMap<>();

    @Override
    public JobPostingRecommendation save(JobPostingRecommendation recommendation) {
        Long recommendationId = recommendation.recommendationId() == null
                ? sequence.getAndIncrement()
                : recommendation.recommendationId();

        JobPostingRecommendation savedRecommendation = recommendation.withId(recommendationId);
        recommendations.put(recommendationId, savedRecommendation);

        return savedRecommendation;
    }

    @Override
    public Optional<JobPostingRecommendation> findLatestByUserId(Long userId) {
        return recommendations.values().stream()
                .filter(recommendation -> recommendation.userId().equals(userId))
                .max(Comparator.comparing(JobPostingRecommendation::createdAt));
    }

    @Override
    public Optional<JobPostingRecommendation> findByIdAndUserId(Long recommendationId, Long userId) {
        return Optional.ofNullable(recommendations.get(recommendationId))
                .filter(recommendation -> recommendation.userId().equals(userId));
    }
}
