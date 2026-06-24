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
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// 항목별 점수 결과에 따른 항목별 강점 혹은 보완점 피드백 생성
// 80점 이상은 백엔드 출력, 79점 이하는 AI 보완
@Service
public class ResumeFeedbackGenerator {

    private final AiPromptRepository
            aiPromptRepository;

    private final ResumeImprovementAiPort
            resumeImprovementAiPort;

    public ResumeFeedbackGenerator(
            AiPromptRepository aiPromptRepository,
            ResumeImprovementAiPort resumeImprovementAiPort
    ) {
        this.aiPromptRepository =
                aiPromptRepository;
        this.resumeImprovementAiPort =
                resumeImprovementAiPort;
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

        Map<ResumeReviewSection, SectionFeedbackResult>
                feedbackBySection =
                new EnumMap<>(
                        ResumeReviewSection.class
                );

        sectionScores.stream()
                .filter(section -> section.score() >= 80)
                .map(SectionFeedbackResult::strength)
                .forEach(feedback ->
                        feedbackBySection.put(
                                feedback.section(),
                                feedback
                        )
                );

        List<SectionScoreResult> improvementTargets =
                sectionScores.stream()
                        .filter(section ->
                                section.score() < 80
                        )
                        .toList();

        if (!improvementTargets.isEmpty()) {
            addImprovementFeedbacks(
                    improvementTargets,
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

    private void addImprovementFeedbacks(
            List<SectionScoreResult> targets,
            Map<ResumeReviewSection, SectionFeedbackResult>
                    feedbackBySection
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

        for (SectionFeedbackResult improvement
                : improvements) {
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
                                    != ResumeFeedbackType
                                    .IMPROVEMENT) {
                                throw new BusinessException(
                                        ErrorCode
                                                .RESUME_INVALID_REVIEW_FEEDBACK
                                );
                            }

                            return feedback.section();
                        })
                        .collect(Collectors.toSet());

        if (improvements.size()
                != targetSections.size()
                || !resultSections.equals(targetSections)) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
            );
        }
    }

    private SectionFeedbackResult requireFeedback(
            SectionScoreResult section,
            Map<ResumeReviewSection, SectionFeedbackResult>
                    feedbackBySection
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