package com.sashimi.ai.infrastructure.persistence;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.ai.domain.model.AiPromptType;
import com.sashimi.ai.domain.repository.AiPromptRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 개발 초기 단계에서 사용하는 임시 AI 프롬프트 저장소 Adapter.
 */
@Repository
public class InMemoryAiPromptRepositoryAdapter implements AiPromptRepository {

    private final AiPrompt resumeReviewPrompt = new AiPrompt(
            1L,
            "이력서 평가 프롬프트",
            AiPromptType.RESUME_REVIEW,
            "아래 이력서를 평가해 주세요. 이력서 내용: {resumeContent}",
            1,
            true
    );

    @Override
    public Optional<AiPrompt> findActiveByType(AiPromptType promptType) {
        if (resumeReviewPrompt.promptType() == promptType && resumeReviewPrompt.isActive()) {
            return Optional.of(resumeReviewPrompt);
        }

        return Optional.empty();
    }
}
