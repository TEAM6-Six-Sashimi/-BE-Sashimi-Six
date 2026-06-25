package com.sashimi.ai.infrastructure.prompt;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.recommendation.application.port.JobPostingContentExtractor;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import org.springframework.stereotype.Component;

@Component
public class JobPostingRecommendationPromptBuilder {

    private final JobPostingContentExtractor contentExtractor;

    public JobPostingRecommendationPromptBuilder(
            JobPostingContentExtractor contentExtractor
    ) {
        this.contentExtractor = contentExtractor;
    }

    public String build(JobPostingRecommendation recommendation, AiPrompt prompt) {
        String jobPostingInput = contentExtractor.extract(
                recommendation.inputType(),
                recommendation.sourceUrl(),
                recommendation.rawContent()
        );

        String userPrompt = prompt.prompt()
                .replace("{resumeBased}", String.valueOf(recommendation.resumeBased()))
                .replace("{jobPostingContent}", safe(jobPostingInput));

        return """
            You are an AI assistant for an LMS job posting recommendation feature.

            Analyze the job posting and return ONLY valid JSON.
            Do not include markdown fences.
            Do not include explanation outside JSON.

            MVP scope:
            - Support one job role per job posting.
            - If the posting contains multiple roles, choose the most central role and summarize only that role.
            - Extract a job posting summary.
            - Analyze fit against the user's resume information if resumeBased is true.
            - If resumeBased is false, still provide summary, recommended certificates, and courses, but use NOT_SATISFIED or PARTIALLY_SATISFIED conservatively for fitAnalysis.
            - Recommend certificates that the user does not already have when possible.
            - Recommend courses related to recommended certificates or missing skills.
            - Return Korean text for human-facing fields.

            Important enum rules:
            - fitAnalysis.*.status must be one of:
              SATISFIED, PARTIALLY_SATISFIED, NOT_SATISFIED
            - fitAnalysis.education.category must be EDUCATION
            - fitAnalysis.career.category must be CAREER
            - fitAnalysis.certification.category must be CERTIFICATION

            Required JSON format:
            {
              "summary": {
                "jobRole": "프론트엔드 개발자",
                "requiredQualifications": [
                  "React/TypeScript/Next.js 실무 경험 3년 이상",
                  "REST API 연동 및 상태관리 경험"
                ],
                "preferredQualifications": [
                  "GraphQL 또는 Apollo Client 경험",
                  "AWS S3, CloudFront 등 클라우드 서비스 활용 경험"
                ],
                "experienceRequirement": "관련 경력 3년 이상",
                "mainTaskSummary": "사용자 중심의 프론트엔드 개발 및 유지보수"
              },
              "fitAnalysis": {
                "education": {
                  "category": "EDUCATION",
                  "status": "SATISFIED",
                  "requiredCondition": "학사 이상",
                  "userCondition": "컴퓨터공학과 학사",
                  "comment": "학력은 공고에서 요구하는 조건을 충족합니다.",
                  "missingItems": []
                },
                "career": {
                  "category": "CAREER",
                  "status": "PARTIALLY_SATISFIED",
                  "requiredCondition": "관련 경력 3년 이상",
                  "userCondition": "프론트엔드 개발 1년 6개월",
                  "comment": "관련 경력이 있으나 요구 연차에는 다소 부족합니다.",
                  "missingItems": ["관련 경력 1년 6개월"]
                },
                "certification": {
                  "category": "CERTIFICATION",
                  "status": "PARTIALLY_SATISFIED",
                  "requiredCondition": "정보처리기사, SQLD",
                  "userCondition": "정보처리기사",
                  "comment": "필수 자격증은 보유했지만 SQLD를 취득하면 적합도를 높일 수 있습니다.",
                  "missingItems": ["SQLD"]
                },
                "overallComments": [
                  "학력 조건은 충족합니다.",
                  "경력은 일부 충족 상태입니다.",
                  "SQLD 자격증 취득 시 데이터 역량을 보완할 수 있습니다."
                ]
              },
              "certificates": [
                {
                  "certificationId": null,
                  "name": "정보처리기사",
                  "reason": "소프트웨어 개발 전반의 기본 역량을 증명할 수 있는 자격증입니다.",
                  "relatedSkills": ["소프트웨어 개발", "데이터베이스"],
                  "difficulty": "보통"
                }
              ],
              "courses": [
                {
                  "courseId": null,
                  "title": "추천 강의명",
                  "instructor": "강사명",
                  "matchedSkill": "SQL",
                  "reason": "부족한 역량 보완에 도움이 되는 강의입니다."
                }
              ]
            }

            Prompt name: %s
            Prompt version: %d

            User prompt:
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