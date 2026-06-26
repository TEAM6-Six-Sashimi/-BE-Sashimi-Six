package com.sashimi.recommendation.application.service;

import com.sashimi.ai.application.policy.AiFeatureAccessPolicy;
import com.sashimi.ai.metric.AiMetrics;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.recommendation.application.command.CreateJobPostingRecommendationCommand;
import com.sashimi.recommendation.application.policy.JobPostingRecommendationPolicy;
import com.sashimi.recommendation.application.usecase.JobPostingRecommendationCommandUseCase;
import com.sashimi.recommendation.application.usecase.JobPostingRecommendationQueryUseCase;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.domain.repository.JobPostingRecommendationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class JobPostingRecommendationService implements
        JobPostingRecommendationCommandUseCase,
        JobPostingRecommendationQueryUseCase {

    private final JobPostingRecommendationRepository recommendationRepository;
    private final JobPostingRecommendationPolicy recommendationPolicy;
    private final JobPostingRecommendationAsyncService asyncService;
    private final JobPostingResumeSummaryBuilder resumeSummaryBuilder;
    private final AiFeatureAccessPolicy aiFeatureAccessPolicy;

    public JobPostingRecommendationService(
            JobPostingRecommendationRepository recommendationRepository,
            JobPostingRecommendationPolicy recommendationPolicy,
            JobPostingRecommendationAsyncService asyncService,
            JobPostingResumeSummaryBuilder resumeSummaryBuilder,
            AiFeatureAccessPolicy aiFeatureAccessPolicy
    ) {
        this.recommendationRepository = recommendationRepository;
        this.recommendationPolicy = recommendationPolicy;
        this.asyncService = asyncService;
        this.resumeSummaryBuilder = resumeSummaryBuilder;
        this.aiFeatureAccessPolicy = aiFeatureAccessPolicy;
    }

    @Override
    @Transactional
    public JobPostingRecommendation create(CreateJobPostingRecommendationCommand command) {
        aiFeatureAccessPolicy.validate(
                command.userId(),
                AiMetrics.FEATURE_JOB_POSTING_RECOMMENDATION
        );

        log.info("채용공고 추천 요청 접수: userId={}, resumeId={}, inputType={}, hasSourceUrl={}, rawContentLength={}",
                command.userId(),
                command.resumeId(),
                command.inputType(),
                command.sourceUrl() != null,
                command.rawContent() == null ? 0 : command.rawContent().length());

        var resume = recommendationPolicy.findResumeForAnalysis(
                command.userId(),
                command.resumeId()
        );

        boolean hasResume = resume.isPresent();

        String resumeContent = resume
                .map(resumeSummaryBuilder::build)
                .orElse("");

        JobPostingRecommendation recommendation = JobPostingRecommendation.create(
                command.userId(),
                command.resumeId(),
                command.inputType(),
                command.sourceUrl(),
                command.rawContent(),
                resumeContent,
                hasResume
        );

        JobPostingRecommendation savedRecommendation =
                recommendationRepository.save(recommendation);

        log.info("채용공고 추천 분석 대기 상태 저장: userId={}, recommendationId={}, resumeBased={}, analysisStatus={}",
                savedRecommendation.userId(),
                savedRecommendation.recommendationId(),
                savedRecommendation.resumeBased(),
                savedRecommendation.analysisStatus());

        asyncService.analyze(
                savedRecommendation.recommendationId(),
                savedRecommendation.userId()
        );

        log.debug("채용공고 추천 비동기 분석 요청 발행: userId={}, recommendationId={}",
                savedRecommendation.userId(),
                savedRecommendation.recommendationId());

        return savedRecommendation;
    }

    @Override
    @Transactional(readOnly = true)
    public JobPostingRecommendation getById(Long userId, Long recommendationId) {
        log.debug("채용공고 추천 결과 조회 요청: userId={}, recommendationId={}",
                userId,
                recommendationId);

        JobPostingRecommendation recommendation =
                recommendationRepository.findByIdAndUserId(recommendationId, userId)
                        .orElseThrow(() -> {
                            log.warn("채용공고 추천 결과 조회 실패: userId={}, recommendationId={}",
                                    userId,
                                    recommendationId);
                            return new BusinessException(
                                    ErrorCode.JOB_POSTING_RECOMMENDATION_NOT_FOUND
                            );
                        });

        log.debug("채용공고 추천 결과 조회 성공: userId={}, recommendationId={}, analysisStatus={}",
                recommendation.userId(),
                recommendation.recommendationId(),
                recommendation.analysisStatus());

        return recommendation;
    }
}