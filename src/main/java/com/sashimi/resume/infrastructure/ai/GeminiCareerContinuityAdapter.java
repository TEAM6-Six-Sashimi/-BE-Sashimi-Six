package com.sashimi.resume.infrastructure.ai;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.ai.infrastructure.gemini.GeminiTextClient;
import com.sashimi.ai.infrastructure.common.AiResponseCleaner;
import com.sashimi.ai.infrastructure.prompt.CareerContinuityPromptBuilder;
import com.sashimi.ai.metric.AiMetrics;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.application.port.CareerContinuityAiPort;
import com.sashimi.resume.application.result.CareerContinuityResult;
import com.sashimi.resume.domain.model.ResumeCareer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@Profile("gemini")
public class GeminiCareerContinuityAdapter
        implements CareerContinuityAiPort {

    private final ObjectMapper objectMapper;
    private final CareerContinuityPromptBuilder promptBuilder;
    private final GeminiTextClient geminiTextClient;

    public GeminiCareerContinuityAdapter(
            ObjectMapper objectMapper,
            CareerContinuityPromptBuilder promptBuilder,
            GeminiTextClient geminiTextClient
    ) {
        this.objectMapper = objectMapper;
        this.promptBuilder = promptBuilder;
        this.geminiTextClient = geminiTextClient;
    }

    @Override
    public CareerContinuityResult evaluate(
            List<ResumeCareer> careers,
            AiPrompt prompt
    ) {
        String input = promptBuilder.build(
                careers,
                prompt
        );

        String generatedText =
                geminiTextClient.generate(
                        input,
                        AiMetrics.FEATURE_RESUME_REVIEW
                );

        return parseResult(generatedText);
    }

    private CareerContinuityResult parseResult(
            String generatedText
    ) {
        try {
            String jsonText =
                    AiResponseCleaner.removeMarkdownFence(
                            generatedText
                    );

            JsonNode root = objectMapper.readTree(
                    jsonText
            );

            JsonNode scoreNode =
                    root.get("continuityScore");

            if (scoreNode == null
                    || !scoreNode.isInt()) {
                throw new IllegalArgumentException(
                        "업무 연속성 점수가 없습니다."
                );
            }

            int score = scoreNode.intValue();

            if (score != 10
                    && score != 20
                    && score != 30) {
                throw new IllegalArgumentException(
                        "허용되지 않은 업무 연속성 점수입니다."
                );
            }

            return new CareerContinuityResult(score);
        } catch (Exception exception) {
            throw new BusinessException(
                    ErrorCode.AI_RESPONSE_PARSE_FAILED
            );
        }
    }
}