package com.sashimi.ai.infrastructure.persistence;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.ai.domain.model.AiPromptType;
import com.sashimi.ai.domain.repository.AiPromptRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("local & !gemini")
public class InMemoryAiPromptRepositoryAdapter
        implements AiPromptRepository {

    private final AiPrompt careerContinuityPrompt =
            new AiPrompt(
                    2L,
                    "경력 업무 연속성 평가 프롬프트",
                    AiPromptType.RESUME_CAREER_CONTINUITY,
                    "다음 경력의 직무 흐름을 평가해주세요. 경력 목록: {careerHistory}",
                    1,
                    true
            );

    private final AiPrompt resumeImprovementPrompt =
            new AiPrompt(
                    3L,
                    "이력서 보완 피드백 프롬프트",
                    AiPromptType.RESUME_IMPROVE,
                    "다음 평가 영역에 대한 짧은 보완 문장을 생성해주세요. 평가 영역: {sections}",
                    1,
                    true
            );

    private final AiPrompt jobPostingAnalysisPrompt =
            new AiPrompt(
                    4L,
                    "채용공고 분석 프롬프트",
                    AiPromptType.JOB_POSTING_ANALYSIS,
                    """
                    채용공고를 분석해주세요.
                    이력서 기반 여부: {resumeBased}
                    채용공고 내용: {jobPostingContent}
                    """,
                    1,
                    true
            );

    @Override
    public Optional<AiPrompt> findActiveByType(
            AiPromptType promptType
    ) {
        if (matches(
                careerContinuityPrompt,
                promptType
        )) {
            return Optional.of(
                    careerContinuityPrompt
            );
        }

        if (matches(
                resumeImprovementPrompt,
                promptType
        )) {
            return Optional.of(
                    resumeImprovementPrompt
            );
        }

        if (matches(
                jobPostingAnalysisPrompt,
                promptType
        )) {
            return Optional.of(
                    jobPostingAnalysisPrompt
            );
        }

        return Optional.empty();
    }

    private boolean matches(
            AiPrompt prompt,
            AiPromptType promptType
    ) {
        return prompt.promptType() == promptType
                && prompt.isActive();
    }
}