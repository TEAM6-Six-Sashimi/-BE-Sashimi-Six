package com.sashimi.resume.application.service;

import com.sashimi.ai.application.policy.AiFeatureAccessPolicy;
import com.sashimi.ai.domain.model.AiFeatureType;
import com.sashimi.ai.infrastructure.persistence.AiRequestHistoryJpaEntity;
import com.sashimi.ai.infrastructure.persistence.SpringDataAiRequestHistoryRepository;
import com.sashimi.ai.metric.AiMetrics;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.application.result.ReviewResumeResult;
import com.sashimi.resume.application.usecase.ReviewResumeUseCase;
import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.repository.ResumeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ResumeReviewService implements ReviewResumeUseCase {

    private final ResumeRepository resumeRepository;
    private final ResumeReviewProcessor resumeReviewProcessor;
    private final AiFeatureAccessPolicy aiFeatureAccessPolicy;
    private final SpringDataAiRequestHistoryRepository aiRequestHistoryRepository;

    public ResumeReviewService(
            ResumeRepository resumeRepository,
            ResumeReviewProcessor resumeReviewProcessor,
            AiFeatureAccessPolicy aiFeatureAccessPolicy,
            SpringDataAiRequestHistoryRepository aiRequestHistoryRepository
    ) {
        this.resumeRepository = resumeRepository;
        this.resumeReviewProcessor = resumeReviewProcessor;
        this.aiFeatureAccessPolicy = aiFeatureAccessPolicy;
        this.aiRequestHistoryRepository = aiRequestHistoryRepository;
    }

    @Override
    public ReviewResumeResult review(
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

        aiRequestHistoryRepository.save(
                AiRequestHistoryJpaEntity.started(
                        userId,
                        AiFeatureType.RESUME_REVIEW,
                        null
                )
        );

        return resumeReviewProcessor.process(
                resume
        );
    }
}