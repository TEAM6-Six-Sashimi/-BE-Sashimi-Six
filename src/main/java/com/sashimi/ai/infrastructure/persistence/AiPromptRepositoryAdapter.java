package com.sashimi.ai.infrastructure.persistence;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.ai.domain.model.AiPromptType;
import com.sashimi.ai.domain.repository.AiPromptRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("gemini")
public class AiPromptRepositoryAdapter implements AiPromptRepository {

    private final SpringDataAiPromptRepository repository;

    public AiPromptRepositoryAdapter(SpringDataAiPromptRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<AiPrompt> findActiveByType(AiPromptType promptType) {
        String purpose = toPurpose(promptType);

        return repository.findFirstByPurposeAndActiveTrueOrderByVersionDesc(purpose)
                .map(entity -> entity.toDomain(promptType));
    }

    private String toPurpose(AiPromptType promptType) {
        return switch (promptType) {
            case RESUME_GENERATE -> "RESUME_GENERATION";
            case RESUME_REVIEW -> "RESUME_EVALUATION";
            case JOB_POSTING_ANALYSIS -> "JOB_ANALYSIS";
            default -> throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        };
    }
}