package com.sashimi.ai.infrastructure.persistence;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.ai.domain.model.AiPromptType;
import jakarta.persistence.*;

@Entity
@Table(name = "ai_prompts")
public class AiPromptJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prompt_id")
    private Long promptId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String purpose;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String prompt;

    @Column(nullable = false)
    private int version;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    protected AiPromptJpaEntity() {
    }

    public AiPrompt toDomain(AiPromptType promptType) {
        return new AiPrompt(
                promptId,
                name,
                promptType,
                prompt,
                version,
                active
        );
    }
}