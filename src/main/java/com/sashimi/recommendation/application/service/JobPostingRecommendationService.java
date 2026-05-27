package com.sashimi.recommendation.application.service;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.ai.domain.model.AiPromptType;
import com.sashimi.ai.domain.repository.AiPromptRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.recommendation.application.command.CreateJobPostingRecommendationCommand;
import com.sashimi.recommendation.application.policy.JobPostingRecommendationPolicy;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzePort;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzeResult;
import com.sashimi.recommendation.application.usecase.JobPostingRecommendationCommandUseCase;
import com.sashimi.recommendation.application.usecase.JobPostingRecommendationQueryUseCase;
import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import com.sashimi.recommendation.domain.model.CourseRecommendation;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.domain.model.RequiredSkillRecommendation;
import com.sashimi.recommendation.domain.repository.JobPostingRecommendationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobPostingRecommendationService implements
        JobPostingRecommendationCommandUseCase,
        JobPostingRecommendationQueryUseCase {

    private final JobPostingRecommendationRepository recommendationRepository;
    private final JobPostingRecommendationAnalyzePort analyzePort;
    private final JobPostingRecommendationPolicy recommendationPolicy;
    private final AiPromptRepository aiPromptRepository;

    public JobPostingRecommendationService(
            JobPostingRecommendationRepository recommendationRepository,
            JobPostingRecommendationAnalyzePort analyzePort,
            JobPostingRecommendationPolicy recommendationPolicy,
            AiPromptRepository aiPromptRepository
    ) {
        this.recommendationRepository = recommendationRepository;
        this.analyzePort = analyzePort;
        this.recommendationPolicy = recommendationPolicy;
        this.aiPromptRepository = aiPromptRepository;
    }

    @Override
    @Transactional
    public JobPostingRecommendation create(CreateJobPostingRecommendationCommand command) {
        boolean hasResume = recommendationPolicy.isResumeBased(command.userId());

        JobPostingRecommendation recommendation = JobPostingRecommendation.create(
                command.userId(),
                command.inputType(),
                command.sourceUrl(),
                command.rawContent(),
                hasResume
        );

        JobPostingRecommendation savedRecommendation = recommendationRepository.save(recommendation);

        AiPrompt prompt = aiPromptRepository.findActiveByType(AiPromptType.JOB_POSTING_ANALYSIS)
                .orElseThrow(() -> new BusinessException(ErrorCode.AI_PROMPT_NOT_FOUND));

        JobPostingRecommendationAnalyzeResult analyzeResult = analyzePort.analyze(savedRecommendation, prompt);

        JobPostingRecommendation analyzedRecommendation = savedRecommendation.analyzed(
                analyzeResult.jobTitle(),
                analyzeResult.matchRate(),
                analyzeResult.requiredSkills(),
                analyzeResult.courses(),
                analyzeResult.certificates()
        );

        return recommendationRepository.save(analyzedRecommendation);
    }

    @Override
    @Transactional(readOnly = true)
    public JobPostingRecommendation getLatest(Long userId) {
        return recommendationRepository.findLatestByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOB_POSTING_RECOMMENDATION_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequiredSkillRecommendation> getLatestSkills(Long userId) {
        return getLatest(userId).requiredSkills();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseRecommendation> getLatestCourses(Long userId) {
        return getLatest(userId).courses();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificateRecommendation> getLatestCertificates(Long userId) {
        return getLatest(userId).certificates();
    }
}