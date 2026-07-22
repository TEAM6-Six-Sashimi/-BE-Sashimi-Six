package com.sashimi.recommendation.infrastructure.persistence;

import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.domain.repository.JobPostingRecommendationRepository;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

@Repository
public class JpaJobPostingRecommendationRepositoryAdapter
        implements JobPostingRecommendationRepository {

    private final SpringDataJobPostingRecommendationRepository repository;
    private final ObjectMapper objectMapper;

    public JpaJobPostingRecommendationRepositoryAdapter(
            SpringDataJobPostingRecommendationRepository repository,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public JobPostingRecommendation save(
            JobPostingRecommendation recommendation
    ) {
        JobPostingRecommendationJpaEntity entity =
                JobPostingRecommendationJpaEntity.from(
                        recommendation,
                        objectMapper
                );

        JobPostingRecommendationJpaEntity savedEntity =
                repository.save(entity);

        return savedEntity.toDomain(objectMapper);
    }

    @Override
    public Optional<JobPostingRecommendation> findLatestByUserId(
            Long userId
    ) {
        return repository.findFirstByUserIdOrderByCreatedAtDescRecommendationIdDesc(
                        userId
                )
                .map(entity -> entity.toDomain(objectMapper));
    }

    @Override
    public Optional<JobPostingRecommendation> findByIdAndUserId(
            Long recommendationId,
            Long userId
    ) {
        return repository.findByRecommendationIdAndUserId(
                        recommendationId,
                        userId
                )
                .map(entity -> entity.toDomain(objectMapper));
    }
}