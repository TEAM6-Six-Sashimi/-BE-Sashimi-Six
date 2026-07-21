package com.sashimi.resume.application.service;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.ai.domain.model.AiPromptType;
import com.sashimi.ai.domain.repository.AiPromptRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.application.port.ResumeImprovementAiPort;
import com.sashimi.resume.application.result.ResumeFeedbackType;
import com.sashimi.resume.application.result.ResumeScoreResult;
import com.sashimi.resume.application.result.SectionFeedbackResult;
import com.sashimi.resume.application.result.SectionScoreResult;
import com.sashimi.resume.domain.model.ResumeReviewSection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ResumeFeedbackGenerator {

    private static final int STRENGTH_SCORE_THRESHOLD = 80;
    private static final int DEFAULT_IMPROVEMENT_SCORE_THRESHOLD = 60;

    private final AiPromptRepository aiPromptRepository;
    private final ResumeImprovementAiPort resumeImprovementAiPort;

    public ResumeFeedbackGenerator(
            AiPromptRepository aiPromptRepository,
            ResumeImprovementAiPort resumeImprovementAiPort
    ) {
        this.aiPromptRepository = aiPromptRepository;
        this.resumeImprovementAiPort = resumeImprovementAiPort;
    }

    public List<SectionFeedbackResult> generate(
            ResumeScoreResult scoreResult
    ) {
        if (scoreResult == null) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
            );
        }

        List<SectionScoreResult> sectionScores =
                scoreResult.sectionScores();

        Map<ResumeReviewSection, SectionFeedbackResult> feedbackBySection =
                new EnumMap<>(ResumeReviewSection.class);

        sectionScores.stream()
                .filter(section ->
                        section.score() >= STRENGTH_SCORE_THRESHOLD
                )
                .map(SectionFeedbackResult::strength)
                .forEach(feedback ->
                        feedbackBySection.put(
                                feedback.section(),
                                feedback
                        )
                );

        List<SectionScoreResult> defaultImprovementTargets =
                sectionScores.stream()
                        .filter(this::requiresDefaultImprovement)
                        .toList();

        addDefaultImprovementFeedbacks(
                defaultImprovementTargets,
                feedbackBySection
        );

        List<SectionScoreResult> aiImprovementTargets =
                sectionScores.stream()
                        .filter(this::requiresAiImprovement)
                        .toList();

        if (!aiImprovementTargets.isEmpty()) {
            addAiImprovementFeedbacksWithFallback(
                    aiImprovementTargets,
                    feedbackBySection
            );
        }

        return sectionScores.stream()
                .map(section ->
                        requireFeedback(
                                section,
                                feedbackBySection
                        )
                )
                .toList();
    }

    private boolean requiresDefaultImprovement(
            SectionScoreResult section
    ) {
        if (section.score() >= STRENGTH_SCORE_THRESHOLD) {
            return false;
        }

        if (section.score() <= DEFAULT_IMPROVEMENT_SCORE_THRESHOLD) {
            return true;
        }

        return section.type() != ResumeReviewSection.CAREER;
    }

    private boolean requiresAiImprovement(
            SectionScoreResult section
    ) {
        return section.type() == ResumeReviewSection.CAREER
                && section.score() < STRENGTH_SCORE_THRESHOLD
                && section.score() > DEFAULT_IMPROVEMENT_SCORE_THRESHOLD;
    }

    private void addDefaultImprovementFeedbacks(
            List<SectionScoreResult> targets,
            Map<ResumeReviewSection, SectionFeedbackResult> feedbackBySection
    ) {
        for (SectionScoreResult target : targets) {
            SectionFeedbackResult feedback =
                    SectionFeedbackResult.improvement(
                            target,
                            defaultImprovementMessage(target.type())
                    );

            SectionFeedbackResult previous =
                    feedbackBySection.put(
                            feedback.section(),
                            feedback
                    );

            if (previous != null) {
                throw new BusinessException(
                        ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
                );
            }
        }
    }

    private String defaultImprovementMessage(
            ResumeReviewSection section
    ) {
        return switch (section) {
            case EDUCATION ->
                    "학력 정보를 구체적으로 작성하면 학력 사항의 완성도를 높일 수 있습니다.";
            case CAREER ->
                    "수행한 주요 업무와 프로젝트 성과를 중심으로 경력 사항을 구체적으로 작성해 주세요.";
            case CERTIFICATE ->
                    "보유한 자격증 정보를 추가하면 자격증 사항의 완성도를 높일 수 있습니다.";
        };
    }

    private void addAiImprovementFeedbacksWithFallback(
            List<SectionScoreResult> targets,
            Map<ResumeReviewSection, SectionFeedbackResult> feedbackBySection
    ) {
        try {
            addAiImprovementFeedbacks(
                    targets,
                    feedbackBySection
            );
        } catch (RuntimeException exception) {
            log.warn(
                    "이력서 AI 보완 피드백 생성 실패. 기본 피드백으로 대체합니다. reason={}",
                    exception.getClass().getSimpleName()
            );

            addDefaultImprovementFeedbacks(
                    targets,
                    feedbackBySection
            );
        }
    }

    private void addAiImprovementFeedbacks(
            List<SectionScoreResult> targets,
            Map<ResumeReviewSection, SectionFeedbackResult> feedbackBySection
    ) {
        AiPrompt prompt = aiPromptRepository
                .findActiveByType(
                        AiPromptType.RESUME_IMPROVE
                )
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.AI_PROMPT_NOT_FOUND
                        )
                );

        List<SectionFeedbackResult> improvements =
                resumeImprovementAiPort.generate(
                        targets,
                        prompt
                );

        validateImprovements(
                targets,
                improvements
        );

        for (SectionFeedbackResult improvement : improvements) {
            if (improvement == null) {
                throw new BusinessException(
                        ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
                );
            }

            SectionFeedbackResult previous =
                    feedbackBySection.put(
                            improvement.section(),
                            improvement
                    );

            if (previous != null) {
                throw new BusinessException(
                        ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
                );
            }
        }
    }

    private void validateImprovements(
            List<SectionScoreResult> targets,
            List<SectionFeedbackResult> improvements
    ) {
        if (improvements == null) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
            );
        }

        Set<ResumeReviewSection> targetSections =
                targets.stream()
                        .map(SectionScoreResult::type)
                        .collect(Collectors.toSet());

        Set<ResumeReviewSection> resultSections =
                improvements.stream()
                        .map(feedback -> {
                            if (feedback == null
                                    || feedback.type() == null
                                    || feedback.type()
                                    != ResumeFeedbackType.IMPROVEMENT) {
                                throw new BusinessException(
                                        ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
                                );
                            }

                            return feedback.section();
                        })
                        .collect(Collectors.toSet());

        if (improvements.size() != targetSections.size()
                || !resultSections.equals(targetSections)) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
            );
        }
    }

    private SectionFeedbackResult requireFeedback(
            SectionScoreResult section,
            Map<ResumeReviewSection, SectionFeedbackResult> feedbackBySection
    ) {
        SectionFeedbackResult feedback =
                feedbackBySection.get(
                        section.type()
                );

        if (feedback == null) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
            );
        }

        return feedback;
    }
}