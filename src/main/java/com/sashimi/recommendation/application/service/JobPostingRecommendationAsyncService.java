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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class JobPostingRecommendationAsyncService {

    private final JobPostingRecommendationRepository recommendationRepository;
    private final JobPostingRecommendationAnalyzePort analyzePort;
    private final AiPromptRepository aiPromptRepository;
    private final ApplicationEventPublisher eventPublisher;

    public JobPostingRecommendationAsyncService(
            JobPostingRecommendationRepository recommendationRepository,
            JobPostingRecommendationAnalyzePort analyzePort,
            AiPromptRepository aiPromptRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.recommendationRepository = recommendationRepository;
        this.analyzePort = analyzePort;
        this.aiPromptRepository = aiPromptRepository;
        this.eventPublisher = eventPublisher;
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

            JobPostingRecommendation analyzedRecommendation = recommendation.analyzed(
                    analyzeResult.jobTitle(),
                    analyzeResult.matchRate(),
                    analyzeResult.requiredSkills(),
                    analyzeResult.courses(),
                    analyzeResult.certificates()
            );

            JobPostingRecommendation savedRecommendation = recommendationRepository.save(analyzedRecommendation);

            log.info("🗃️ 채용공고 추천 비동기 분석 완료: userId={}, recommendationId={}, jobTitle={}, matchRate={}",
                    savedRecommendation.userId(),
                    savedRecommendation.recommendationId(),
                    savedRecommendation.jobTitle(),
                    savedRecommendation.matchRate());

            eventPublisher.publishEvent(
                    new JobPostingRecommendationAnalyzedEvent(
                            savedRecommendation.userId(),
                            savedRecommendation.recommendationId(),
                            savedRecommendation.jobTitle(),
                            savedRecommendation.matchRate(),
                            savedRecommendation.createdAt()
                    )
            );

        } catch (Exception e) {
            log.error("🗃️ 채용공고 AI 분석 실패. recommendationId={}, userId={}", recommendationId, userId, e);

            JobPostingRecommendation failedRecommendation = recommendation.failed();
            recommendationRepository.save(failedRecommendation);
        }
    }
}