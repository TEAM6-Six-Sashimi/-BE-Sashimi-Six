package com.sashimi.resume.application.service;

import com.sashimi.ai.application.policy.AiFeatureAccessPolicy;
import com.sashimi.ai.domain.model.AiFeatureType;
import com.sashimi.ai.infrastructure.persistence.AiRequestHistoryJpaEntity;
import com.sashimi.ai.infrastructure.persistence.SpringDataAiRequestHistoryRepository;
import com.sashimi.ai.metric.AiMetrics;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.application.result.ReviewResumeResult;
import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.repository.ResumeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
public class ResumeReviewTransactionService {

    private final ResumeRepository resumeRepository;
    private final AiFeatureAccessPolicy aiFeatureAccessPolicy;
    private final SpringDataAiRequestHistoryRepository aiRequestHistoryRepository;
    private final ObjectMapper objectMapper;

    public ResumeReviewTransactionService(
            ResumeRepository resumeRepository,
            AiFeatureAccessPolicy aiFeatureAccessPolicy,
            SpringDataAiRequestHistoryRepository aiRequestHistoryRepository,
            ObjectMapper objectMapper
    ) {
        this.resumeRepository = resumeRepository;
        this.aiFeatureAccessPolicy = aiFeatureAccessPolicy;
        this.aiRequestHistoryRepository = aiRequestHistoryRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ResumeReviewPreparation prepareReview(
            Long resumeId,
            Long userId
    ) {
        aiFeatureAccessPolicy.validate(
                userId,
                AiMetrics.FEATURE_RESUME_REVIEW
        );

        Resume resume = resumeRepository
                .findByIdAndUserId(resumeId, userId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.RESUME_NOT_FOUND
                        )
                );

        AiRequestHistoryJpaEntity history = aiRequestHistoryRepository.save(
                AiRequestHistoryJpaEntity.started(
                        userId,
                        AiFeatureType.RESUME_REVIEW,
                        String.valueOf(resumeId)
                )
        );

        return new ResumeReviewPreparation(
                history.getId(),
                resume
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completeReview(
            Long historyId,
            ReviewResumeResult result
    ) {
        AiRequestHistoryJpaEntity history = aiRequestHistoryRepository
                .findById(historyId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.AI_RESPONSE_PARSE_FAILED
                ));

        history.complete(
                writeJson(result)
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failReview(
            Long historyId,
            RuntimeException exception
    ) {
        aiRequestHistoryRepository.findById(historyId)
                .ifPresent(history ->
                        history.fail(
                                exception.getClass().getSimpleName()
                        )
                );
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new BusinessException(
                    ErrorCode.AI_RESPONSE_PARSE_FAILED
            );
        }
    }
}