package com.sashimi.resume.application.service;

import com.sashimi.ai.domain.model.AiFeatureType;
import com.sashimi.ai.domain.model.AiRequestStatus;
import com.sashimi.ai.infrastructure.persistence.SpringDataAiRequestHistoryRepository;
import com.sashimi.ai.metric.AiMetrics;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.application.result.ReviewResumeResult;
import com.sashimi.resume.application.usecase.ReviewResumeUseCase;
import com.sashimi.resume.domain.repository.ResumeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

@Service
public class ResumeReviewService implements ReviewResumeUseCase {

    private final ResumeRepository resumeRepository;
    private final ResumeReviewProcessor resumeReviewProcessor;
    private final ResumeReviewTransactionService transactionService;
    private final SpringDataAiRequestHistoryRepository aiRequestHistoryRepository;
    private final AiMetrics aiMetrics;
    private final ObjectMapper objectMapper;

    public ResumeReviewService(
            ResumeRepository resumeRepository,
            ResumeReviewProcessor resumeReviewProcessor,
            ResumeReviewTransactionService transactionService,
            SpringDataAiRequestHistoryRepository aiRequestHistoryRepository,
            AiMetrics aiMetrics,
            ObjectMapper objectMapper
    ) {
        this.resumeRepository = resumeRepository;
        this.resumeReviewProcessor = resumeReviewProcessor;
        this.transactionService = transactionService;
        this.aiRequestHistoryRepository = aiRequestHistoryRepository;
        this.aiMetrics = aiMetrics;
        this.objectMapper = objectMapper;
    }

    @Override
    public ReviewResumeResult review(
            Long resumeId,
            Long userId
    ) {
        ResumeReviewPreparation preparation =
                transactionService.prepareReview(
                        resumeId,
                        userId
                );

        long startedAt = System.currentTimeMillis();

        aiMetrics.incrementRequestStarted(
                AiMetrics.FEATURE_RESUME_REVIEW
        );

        try {
            ReviewResumeResult result = resumeReviewProcessor.process(
                    preparation.resume()
            );

            transactionService.completeReview(
                    preparation.historyId(),
                    result
            );

            aiMetrics.incrementRequestSuccess(
                    AiMetrics.FEATURE_RESUME_REVIEW
            );

            aiMetrics.recordRequestDuration(
                    AiMetrics.FEATURE_RESUME_REVIEW,
                    "SUCCESS",
                    System.currentTimeMillis() - startedAt
            );

            return result;
        } catch (RuntimeException exception) {
            transactionService.failReview(
                    preparation.historyId(),
                    exception
            );

            aiMetrics.incrementRequestFailed(
                    AiMetrics.FEATURE_RESUME_REVIEW,
                    exception.getClass().getSimpleName()
            );

            aiMetrics.recordRequestDuration(
                    AiMetrics.FEATURE_RESUME_REVIEW,
                    "FAILED",
                    System.currentTimeMillis() - startedAt
            );

            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public Optional<ReviewResumeResult> getLatestReview(
            Long resumeId,
            Long userId
    ) {
        resumeRepository
                .findByIdAndUserId(resumeId, userId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.RESUME_NOT_FOUND
                        )
                );

        return aiRequestHistoryRepository
                .findFirstByUserIdAndFeatureTypeAndStatusAndRequestSnapshotJsonOrderByCreatedAtDesc(
                        userId,
                        AiFeatureType.RESUME_REVIEW,
                        AiRequestStatus.COMPLETED,
                        String.valueOf(resumeId)
                )
                .filter(history -> history.getResultJson() != null
                        && !history.getResultJson().isBlank())
                .map(history -> readJson(
                        history.getResultJson(),
                        ReviewResumeResult.class
                ));
    }

    private <T> T readJson(
            String json,
            Class<T> type
    ) {
        try {
            return objectMapper.readValue(
                    json,
                    type
            );
        } catch (Exception exception) {
            throw new BusinessException(
                    ErrorCode.AI_RESPONSE_PARSE_FAILED
            );
        }
    }
}