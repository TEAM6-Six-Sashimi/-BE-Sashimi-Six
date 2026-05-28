package com.sashimi.resume.infrastructure.ai;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.ai.infrastructure.gemini.GeminiResponseCleaner;
import com.sashimi.ai.infrastructure.gemini.GeminiTextClient;
import com.sashimi.ai.infrastructure.prompt.ResumeReviewPromptBuilder;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.application.port.ResumeAiReviewPort;
import com.sashimi.resume.application.port.ResumeAiReviewResult;
import com.sashimi.resume.domain.model.Resume;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@Profile("gemini")
public class GeminiResumeReviewAdapter implements ResumeAiReviewPort {

    private final ObjectMapper objectMapper;
    private final ResumeReviewPromptBuilder promptBuilder;
    private final GeminiTextClient geminiTextClient;

    public GeminiResumeReviewAdapter(
            ObjectMapper objectMapper,
            ResumeReviewPromptBuilder promptBuilder,
            GeminiTextClient geminiTextClient
    ) {
        this.objectMapper = objectMapper;
        this.promptBuilder = promptBuilder;
        this.geminiTextClient = geminiTextClient;
    }

    @Override
    public ResumeAiReviewResult review(Resume resume, AiPrompt prompt) {
        String input = promptBuilder.build(resume, prompt);

        String generatedText = geminiTextClient.generate(input);

        return parseReviewResult(generatedText);
    }

    private ResumeAiReviewResult parseReviewResult(String generatedText) {
        try {
            String jsonText = GeminiResponseCleaner.removeMarkdownFence(generatedText);
            JsonNode root = objectMapper.readTree(jsonText);

            return new ResumeAiReviewResult(
                    root.path("overallScore").decimalValue(),
                    root.path("strengths").asText(),
                    root.path("weaknesses").asText(),
                    root.path("suggestions").asText(),
                    jsonText
            );

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.AI_RESPONSE_PARSE_FAILED);
        }
    }
}