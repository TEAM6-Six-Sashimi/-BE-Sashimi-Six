package com.sashimi.ai.infrastructure.persistence;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.ai.domain.model.AiPromptType;
import com.sashimi.ai.domain.repository.AiPromptRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("openai")
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

    private String toPurpose(
            AiPromptType promptType
    ) {
        return switch (promptType) {
            case RESUME_GENERATE ->
                    "RESUME_GENERATION";

            case RESUME_IMPROVE ->
                    "RESUME_IMPROVEMENT";

            case RESUME_CAREER_CONTINUITY ->
                    "RESUME_CAREER_CONTINUITY";

            case CERTIFICATE_VERIFY ->
                    "CERTIFICATE_VERIFY";

            case JOB_POSTING_ANALYSIS ->
                    "JOB_ANALYSIS";
        };
    }
}