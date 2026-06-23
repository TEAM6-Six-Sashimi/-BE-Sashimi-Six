package com.sashimi.ai.infrastructure.prompt;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.application.result.SectionScoreResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResumeImprovementPromptBuilder {

    public String build(
            List<SectionScoreResult> sections,
            AiPrompt prompt
    ) {
        validate(sections, prompt);

        String sectionContent =
                buildSectionContent(sections);

        String userPrompt = prompt.prompt()
                .replace(
                        "{sections}",
                        sectionContent
                );

        return """
                You are an AI assistant that generates short improvement
                feedback for an LMS resume review feature.

                Rules:
                - Return ONLY valid JSON.
                - Do not include markdown fences.
                - Do not include explanations outside JSON.
                - Generate exactly one improvement message
                  for each provided section.
                - Do not add sections that were not provided.
                - Do not recalculate scores.
                - Do not change scores or grades.
                - Write all messages in Korean.
                - Keep each message short and constructive.
                - Do not evaluate school or company reputation.
                - Do not evaluate age, gender, region,
                  or personal background.
                - Do not mention technical skills
                  that are not included in the input.
                - Certificate policy is temporarily based
                  on the verified certificate count.

                Required JSON format:
                {
                  "improvements": [
                    {
                      "section": "CAREER",
                      "message": "경력 정보를 추가하면 경력 사항의 완성도를 높일 수 있습니다."
                    }
                  ]
                }

                Prompt name: %s
                Prompt version: %d

                Input:
                %s
                """.formatted(
                prompt.name(),
                prompt.version(),
                userPrompt
        );
    }

    private void validate(
            List<SectionScoreResult> sections,
            AiPrompt prompt
    ) {
        if (sections == null || sections.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
            );
        }

        if (sections.stream().anyMatch(
                section -> section.score() >= 80
        )) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
            );
        }

        if (prompt == null) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
            );
        }
    }

    private String buildSectionContent(
            List<SectionScoreResult> sections
    ) {
        return sections.stream()
                .map(section ->
                        """
                        - section: %s
                          label: %s
                          score: %d
                          grade: %s
                        """.formatted(
                                section.type(),
                                section.label(),
                                section.score(),
                                section.grade()
                        ).strip()
                )
                .reduce(
                        (first, second) ->
                                first + "\n" + second
                )
                .orElseThrow();
    }
}