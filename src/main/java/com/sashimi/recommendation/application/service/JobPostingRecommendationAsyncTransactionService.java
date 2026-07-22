package com.sashimi.recommendation.application.service;

import com.sashimi.ai.infrastructure.persistence.SpringDataAiRequestHistoryRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.domain.repository.JobPostingRecommendationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobPostingRecommendationAsyncTransactionService {

    private final JobPostingRecommendationRepository recommendationRepository;
    private final SpringDataAiRequestHistoryRepository aiRequestHistoryRepository;

    public JobPostingRecommendationAsyncTransactionService(
            JobPostingRecommendationRepository recommendationRepository,
            SpringDataAiRequestHistoryRepository aiRequestHistoryRepository
    ) {
        this.recommendationRepository = recommendationRepository;
        this.aiRequestHistoryRepository = aiRequestHistoryRepository;
    }

    @Transactional(readOnly = true)
    public JobPostingRecommendation getRecommendation(
            Long recommendationId,
            Long userId
    ) {
        return recommendationRepository.findByIdAndUserId(
                        recommendationId,
                        userId
                )
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.JOB_POSTING_RECOMMENDATION_NOT_FOUND
                ));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public JobPostingRecommendation completeAnalysis(
            JobPostingRecommendation analyzedRecommendation,
            Long historyId
    ) {
        JobPostingRecommendation savedRecommendation =
                recommendationRepository.save(
                        analyzedRecommendation
                );

        aiRequestHistoryRepository.findById(historyId)
                .ifPresent(history -> history.complete(null));

        return savedRecommendation;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failAnalysis(
            Long recommendationId,
            Long userId,
            Long historyId,
            Exception exception
    ) {
        recommendationRepository.findByIdAndUserId(
                        recommendationId,
                        userId
                )
                .ifPresent(recommendation ->
                        recommendationRepository.save(
                                recommendation.failed()
                        )
                );

        aiRequestHistoryRepository.findById(historyId)
                .ifPresent(history ->
                        history.fail(
                                exception.getClass().getSimpleName()
                        )
                );
    }
}