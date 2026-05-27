package com.sashimi.ai.infrastructure.prompt;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.resume.domain.model.Resume;
import org.springframework.stereotype.Component;

@Component
public class ResumeReviewPromptBuilder {

    public String build(Resume resume, AiPrompt prompt) {
        String userPrompt = prompt.prompt()
                .replace("{resumeContent}", safe(resume.content()))
                .replace("{resumeTitle}", safe(resume.title()));

        return """
                You are an AI assistant for an LMS resume review feature.

                Analyze the user's resume and return ONLY valid JSON.
                Do not include markdown fences.
                Do not include explanation outside JSON.

                Rules:
                - overallScore must be a number from 0 to 100.
                - strengths must summarize the resume's strong points.
                - weaknesses must summarize missing or weak points.
                - suggestions must provide concrete improvement suggestions.
                - aiResult must contain the detailed review result.
                - Return Korean text for all explanation fields.

                Required JSON format:
                {
                  "overallScore": 82,
                  "strengths": "Java/Spring 기반 프로젝트 경험이 강점입니다.",
                  "weaknesses": "성과 지표와 운영 경험 설명이 부족합니다.",
                  "suggestions": "프로젝트별 역할, 문제 해결 과정, 성과를 수치로 보완하면 좋습니다.",
                  "aiResult": "전체적으로 백엔드 개발 역량은 확인되지만 실무 성과 중심의 서술이 필요합니다."
                }

                Prompt name: %s
                Prompt version: %d

                Resume:
                %s
                """.formatted(
                prompt.name(),
                prompt.version(),
                userPrompt
        );
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}