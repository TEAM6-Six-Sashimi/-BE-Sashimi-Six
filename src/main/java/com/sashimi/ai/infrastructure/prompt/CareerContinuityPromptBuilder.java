package com.sashimi.ai.infrastructure.prompt;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.domain.model.ResumeCareer;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CareerContinuityPromptBuilder {

    public String build(
            List<ResumeCareer> careers,
            AiPrompt prompt
    ) {
        if (careers == null || careers.size() < 2) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        if (prompt == null) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        String careerHistory =
                buildCareerHistory(careers);

        String userPrompt = prompt.prompt()
                .replace(
                        "{careerHistory}",
                        careerHistory
                );

        return """
                You are an AI assistant that evaluates career continuity
                for an LMS resume review feature.

                Analyze the user's career history and determine how
                consistent the job fields are.

                Rules:
                - Return ONLY valid JSON.
                - Do not include markdown fences.
                - Do not include explanations outside JSON.
                - Evaluate only job titles and career flow.
                - Do not evaluate company name or reputation.
                - Do not evaluate age, gender, school, region,
                  or personal background.
                - This LMS covers many occupations and certificate fields.
                - It is not limited to software development.
                - Return only one of these scores:
                  - 30: Same or highly similar job field
                  - 20: Related field but different specific role
                  - 10: Mostly unrelated job fields
                - Do not return 0 because the input always contains
                  at least two career records.

                Required JSON format:
                {
                  "continuityScore": 20
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

    private String buildCareerHistory(
            List<ResumeCareer> careers
    ) {
        AtomicInteger order =
                new AtomicInteger(1);

        return careers.stream()
                .sorted(
                        Comparator.comparing(
                                ResumeCareer::startYearMonth
                        )
                )
                .map(career ->
                        "%d. %s".formatted(
                                order.getAndIncrement(),
                                career.jobTitle()
                        )
                )
                .reduce(
                        (first, second) ->
                                first + "\n" + second
                )
                .orElseThrow();
    }
}