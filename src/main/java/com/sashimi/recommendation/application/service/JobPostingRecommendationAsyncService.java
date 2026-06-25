package com.sashimi.recommendation.application.service;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.ai.domain.model.AiPromptType;
import com.sashimi.ai.domain.repository.AiPromptRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.recommendation.application.event.JobPostingRecommendationAnalyzedEvent;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzePort;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzeResult;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.domain.repository.JobPostingRecommendationRepository;
import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.repository.ResumeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class JobPostingRecommendationAsyncService {

    private final JobPostingRecommendationRepository recommendationRepository;
    private final JobPostingRecommendationAnalyzePort analyzePort;
    private final AiPromptRepository aiPromptRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CertificateRecommendationEnricher certificateRecommendationEnricher;
    private final ResumeRepository resumeRepository;
    private final OwnedCertificateRecommendationFilter ownedCertificateRecommendationFilter;
    private final CourseRecommendationMatcher courseRecommendationMatcher;

    public JobPostingRecommendationAsyncService(
            JobPostingRecommendationRepository recommendationRepository,
            JobPostingRecommendationAnalyzePort analyzePort,
            AiPromptRepository aiPromptRepository,
            ApplicationEventPublisher eventPublisher,
            CertificateRecommendationEnricher certificateRecommendationEnricher,
            ResumeRepository resumeRepository,
            OwnedCertificateRecommendationFilter ownedCertificateRecommendationFilter,
            CourseRecommendationMatcher courseRecommendationMatcher
    ) {
        this.recommendationRepository = recommendationRepository;
        this.analyzePort = analyzePort;
        this.aiPromptRepository = aiPromptRepository;
        this.eventPublisher = eventPublisher;
        this.certificateRecommendationEnricher = certificateRecommendationEnricher;
        this.resumeRepository = resumeRepository;
        this.ownedCertificateRecommendationFilter = ownedCertificateRecommendationFilter;
        this.courseRecommendationMatcher = courseRecommendationMatcher;
    }

    @Async
    @Transactional
    public void analyze(Long recommendationId, Long userId) {
        log.info("🗃️ 채용공고 추천 비동기 분석 시작: userId={}, recommendationId={}", userId, recommendationId);

        JobPostingRecommendation recommendation = recommendationRepository.findByIdAndUserId(recommendationId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOB_POSTING_RECOMMENDATION_NOT_FOUND));

        try {
            AiPrompt prompt = aiPromptRepository.findActiveByType(AiPromptType.JOB_POSTING_ANALYSIS)
                    .orElseThrow(() -> new BusinessException(ErrorCode.AI_PROMPT_NOT_FOUND));

            JobPostingRecommendationAnalyzeResult analyzeResult = analyzePort.analyze(recommendation, prompt);

            Optional<Resume> resume = findResumeForCertificateFilter(recommendation);

            var filteredCertificates = ownedCertificateRecommendationFilter.filter(
                    analyzeResult.certificates(),
                    resume.orElse(null)
            );

            var enrichedCertificates = certificateRecommendationEnricher.enrich(
                    filteredCertificates
            );

            var matchedCourses = courseRecommendationMatcher.match(
                    enrichedCertificates
            );

            JobPostingRecommendation analyzedRecommendation = recommendation.analyzed(
                    analyzeResult.summary(),
                    recommendation.resumeBased() ? analyzeResult.fitAnalysis() : null,
                    matchedCourses,
                    enrichedCertificates
            );

            JobPostingRecommendation savedRecommendation = recommendationRepository.save(analyzedRecommendation);

            log.info("채용공고 추천 비동기 분석 완료: userId={}, recommendationId={}, jobRole={}",
                    savedRecommendation.userId(),
                    savedRecommendation.recommendationId(),
                    savedRecommendation.summary() == null ? null : savedRecommendation.summary().jobRole());

            eventPublisher.publishEvent(
                    new JobPostingRecommendationAnalyzedEvent(
                            savedRecommendation.userId(),
                            savedRecommendation.recommendationId(),
                            savedRecommendation.summary() == null ? null : savedRecommendation.summary().jobRole(),
                            LocalDateTime.now()
                    )
            );

        } catch (Exception e) {
            log.error("🗃️ 채용공고 AI 분석 실패. recommendationId={}, userId={}", recommendationId, userId, e);

            JobPostingRecommendation failedRecommendation = recommendation.failed();
            recommendationRepository.save(failedRecommendation);
        }
    }

    private Optional<Resume> findResumeForCertificateFilter(
            JobPostingRecommendation recommendation
    ) {
        if (recommendation.resumeId() == null) {
            return Optional.empty();
        }

        return resumeRepository.findByIdAndUserId(
                recommendation.resumeId(),
                recommendation.userId()
        );
    }
}