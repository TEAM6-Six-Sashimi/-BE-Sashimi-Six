package com.sashimi.recommendation.application.policy;

import com.sashimi.resume.domain.repository.ResumeRepository;
import org.springframework.stereotype.Component;

@Component
public class JobPostingRecommendationPolicy {

    private final ResumeRepository resumeRepository;

    public JobPostingRecommendationPolicy(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public boolean isResumeBased(Long userId) {
        return !resumeRepository.findAllByUserId(userId).isEmpty();
    }
}