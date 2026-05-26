package com.sashimi.recommendation.application.service;

import com.sashimi.recommendation.application.command.CreateJobPostingRecommendationCommand;
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

    public JobPostingRecommendationService(
            JobPostingRecommendationRepository recommendationRepository,
            JobPostingRecommendationAnalyzePort analyzePort
    ) {
        this.recommendationRepository = recommendationRepository;
        this.analyzePort = analyzePort;
    }

    @Override
    @Transactional
    public JobPostingRecommendation create(CreateJobPostingRecommendationCommand command) {

        // TODO: ResumeRepository 연동 후 실제 이력서 보유 여부를 조회하도록 변경한다.
        boolean hasResume = true;

        JobPostingRecommendation recommendation = JobPostingRecommendation.create(
                command.userId(),
                command.inputType(),
                command.sourceUrl(),
                command.rawContent(),
                hasResume
        );

        JobPostingRecommendation savedRecommendation = recommendationRepository.save(recommendation);

        JobPostingRecommendationAnalyzeResult analyzeResult = analyzePort.analyze(savedRecommendation);

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
                .orElseThrow(() -> new IllegalArgumentException("Job posting recommendation not found."));
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
