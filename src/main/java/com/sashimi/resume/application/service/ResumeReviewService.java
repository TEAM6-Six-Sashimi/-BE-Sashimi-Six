package com.sashimi.resume.application.service;

import com.sashimi.ai.application.policy.AiFeatureAccessPolicy;
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

    public ResumeReviewService(
            ResumeRepository resumeRepository,
            ResumeReviewProcessor resumeReviewProcessor,
            AiFeatureAccessPolicy aiFeatureAccessPolicy
    ) {
        this.resumeRepository = resumeRepository;
        this.resumeReviewProcessor = resumeReviewProcessor;
        this.aiFeatureAccessPolicy = aiFeatureAccessPolicy;
    }

    @Override
    public ReviewResumeResult review(
            Long resumeId,
            Long userId
    ) {
        aiFeatureAccessPolicy.validate(userId);

        Resume resume = resumeRepository
                .findByIdAndUserId(resumeId, userId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.RESUME_NOT_FOUND
                        )
                );

        int certificateCount = 0;

        return resumeReviewProcessor.process(
                resume,
                certificateCount
        );
    }
}