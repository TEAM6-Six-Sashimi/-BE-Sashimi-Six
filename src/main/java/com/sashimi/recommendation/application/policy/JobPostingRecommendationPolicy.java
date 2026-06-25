package com.sashimi.recommendation.application.policy;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.repository.ResumeRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JobPostingRecommendationPolicy {

    private final ResumeRepository resumeRepository;

    public JobPostingRecommendationPolicy(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public Optional<Resume> findResumeForAnalysis(Long userId, Long resumeId) {
        if (resumeId == null) {
            return Optional.empty();
        }

        return Optional.of(
                resumeRepository.findByIdAndUserId(resumeId, userId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.RESUME_NOT_FOUND))
        );
    }
}