package com.sashimi.resume.infrastructure.ai;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.ai.infrastructure.gemini.GeminiTextClient;
import com.sashimi.ai.infrastructure.common.AiResponseCleaner;
import com.sashimi.ai.infrastructure.prompt.ResumeImprovementPromptBuilder;
import com.sashimi.ai.metric.AiMetrics;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.application.port.ResumeImprovementAiPort;
import com.sashimi.resume.application.result.SectionFeedbackResult;
import com.sashimi.resume.application.result.SectionScoreResult;
import com.sashimi.resume.domain.model.ResumeReviewSection;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@Profile("gemini")
public class GeminiResumeImprovementAdapter
        implements ResumeImprovementAiPort {

    private final ObjectMapper objectMapper;
    private final ResumeImprovementPromptBuilder promptBuilder;
    private final GeminiTextClient geminiTextClient;

    public GeminiResumeImprovementAdapter(
            ObjectMapper objectMapper,
            ResumeImprovementPromptBuilder promptBuilder,
            GeminiTextClient geminiTextClient
    ) {
        this.objectMapper = objectMapper;
        this.promptBuilder = promptBuilder;
        this.geminiTextClient = geminiTextClient;
    }

    @Override
    public List<SectionFeedbackResult> generate(
            List<SectionScoreResult> sections,
            AiPrompt prompt
    ) {
        String input = promptBuilder.build(
                sections,
                prompt
        );

        String generatedText =
                geminiTextClient.generate(
                        input,
                        AiMetrics.FEATURE_RESUME_REVIEW
                );

        return parseResult(
                generatedText,
                sections
        );
    }

    private List<SectionFeedbackResult> parseResult(
            String generatedText,
            List<SectionScoreResult> requestedSections
    ) {
        try {
            String jsonText =
                    AiResponseCleaner.removeMarkdownFence(
                            generatedText
                    );

            JsonNode root = objectMapper.readTree(
                    jsonText
            );

            JsonNode improvementsNode =
                    root.get("improvements");

            if (improvementsNode == null
                    || !improvementsNode.isArray()) {
                throw new IllegalArgumentException(
                        "improvements 배열이 없습니다."
                );
            }

            Map<ResumeReviewSection, String> messages =
                    parseMessages(
                            improvementsNode,
                            requestedSections
                    );

            return requestedSections.stream()
                    .map(section ->
                            SectionFeedbackResult.improvement(
                                    section,
                                    messages.get(
                                            section.type()
                                    )
                            )
                    )
                    .toList();
        } catch (Exception exception) {
            throw new BusinessException(
                    ErrorCode.AI_RESPONSE_PARSE_FAILED
            );
        }
    }

    private Map<ResumeReviewSection, String> parseMessages(
            JsonNode improvementsNode,
            List<SectionScoreResult> requestedSections
    ) {
        Map<ResumeReviewSection, SectionScoreResult>
                requestedByType =
                new EnumMap<>(
                        ResumeReviewSection.class
                );

        for (SectionScoreResult section
                : requestedSections) {
            requestedByType.put(
                    section.type(),
                    section
            );
        }

        Map<ResumeReviewSection, String> messages =
                new EnumMap<>(
                        ResumeReviewSection.class
                );

        for (JsonNode improvementNode
                : improvementsNode) {
            JsonNode sectionNode =
                    improvementNode.get("section");

            JsonNode messageNode =
                    improvementNode.get("message");

            if (sectionNode == null
                    || !sectionNode.isTextual()
                    || messageNode == null
                    || !messageNode.isTextual()
                    || messageNode.asText().isBlank()) {
                throw new IllegalArgumentException(
                        "보완 피드백 형식이 올바르지 않습니다."
                );
            }

            ResumeReviewSection section;

            try {
                section = ResumeReviewSection.valueOf(
                        sectionNode.asText()
                                .trim()
                                .toUpperCase(Locale.ROOT)
                );
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException(
                        "요청하지 않은 평가 영역이 포함되었습니다."
                );
            }

            if (!requestedByType.containsKey(section)) {
                throw new IllegalArgumentException(
                        "요청하지 않은 평가 영역이 포함되었습니다."
                );
            }

            if (messages.put(
                    section,
                    messageNode.asText()
            ) != null) {
                throw new IllegalArgumentException(
                        "동일한 평가 영역이 중복되었습니다."
                );
            }
        }

        if (messages.size()
                != requestedByType.size()) {
            throw new IllegalArgumentException(
                    "일부 평가 영역의 보완 피드백이 누락되었습니다."
            );
        }

        return messages;
    }
}