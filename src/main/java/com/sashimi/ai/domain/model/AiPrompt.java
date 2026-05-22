package com.sashimi.ai.domain.model;

public class AiPrompt {

    private final Long promptId;
    private final String name;
    private final AiPromptType promptType;
    private final String prompt;
    private final int version;
    private final boolean active;

    public AiPrompt(
            Long promptId,
            String name,
            AiPromptType promptType,
            String prompt,
            int version,
            boolean active
    ) {
        this.promptId = promptId;
        this.name = name;
        this.promptType = promptType;
        this.prompt = prompt;
        this.version = version;
        this.active = active;
    }

    public Long promptId() {
        return promptId;
    }

    public String name() {
        return name;
    }

    public AiPromptType promptType() {
        return promptType;
    }

    public String prompt() {
        return prompt;
    }

    public int version() {
        return version;
    }

    public boolean isActive() {
        return active;
    }
}
