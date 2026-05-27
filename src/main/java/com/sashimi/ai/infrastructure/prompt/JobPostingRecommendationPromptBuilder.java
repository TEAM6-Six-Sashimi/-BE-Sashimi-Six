package com.sashimi.ai.infrastructure.prompt;

import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import org.springframework.stereotype.Component;

@Component
public class JobPostingRecommendationPromptBuilder {

    public String build(JobPostingRecommendation recommendation) {
        String jobPostingInput = switch (recommendation.inputType()) {
            case URL -> """
                    The user submitted a job posting URL.
                    URL: %s

                    URL crawling is not implemented yet.
                    Infer a reasonable analysis only from the URL and general job posting context.
                    """.formatted(recommendation.sourceUrl());

            case TEXT -> """
                    The user submitted this job posting text:

                    %s
                    """.formatted(recommendation.rawContent());
        };

        return """
                You are an AI assistant for an LMS career recommendation feature.

                Analyze the job posting and return ONLY valid JSON.
                Do not include markdown fences.
                Do not include explanation outside JSON.

                Rules:
                - Extract required skills from the job posting.
                - Recommend certificates related to the job role and required skills.
                - Recommend courses that can help the user fill missing skills.
                - If resumeBased is false, set matchRate to null and requiredSkills[].matched to null.
                - If resumeBased is true, estimate matchRate from 0 to 100 and set requiredSkills[].matched to true or false.
                - Return Korean text for course titles, certificate reasons, and recommendation reasons.

                resumeBased: %s

                Required JSON format:
                {
                  "jobTitle": "Frontend Developer",
                  "matchRate": 56,
                  "requiredSkills": [
                    {
                      "name": "React",
                      "category": "Frontend",
                      "matched": true
                    }
                  ],
                  "courses": [
                    {
                      "courseId": 1,
                      "title": "실무에서 바로 쓰는 GraphQL 완전 정복",
                      "instructor": "김민준",
                      "matchedSkill": "GraphQL",
                      "reason": "공고에서 GraphQL 역량을 요구하므로 보완 학습에 적합합니다."
                    }
                  ],
                  "certificates": [
                    {
                      "certificationId": 1,
                      "name": "AWS Cloud Practitioner",
                      "reason": "AWS 클라우드 기초 역량을 증명하는 데 적합합니다.",
                      "relatedSkills": ["AWS"],
                      "difficulty": "쉬움"
                    }
                  ]
                }

                Job posting input:
                %s
                """.formatted(
                recommendation.resumeBased(),
                jobPostingInput
        );
    }
}