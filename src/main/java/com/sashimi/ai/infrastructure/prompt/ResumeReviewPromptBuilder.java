package com.sashimi.ai.infrastructure.prompt;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.resume.domain.model.Resume;
import org.springframework.stereotype.Component;
import com.sashimi.resume.domain.model.ResumeCareer;
import com.sashimi.resume.domain.model.ResumeEducation;

// 해당 클래스는 계산기 클래스 추가한 후 삭제될 예정입니다.
@Component
public class ResumeReviewPromptBuilder {

    public String build(Resume resume, AiPrompt prompt) {
        String userPrompt = prompt.prompt()
                .replace(
                        "{resumeContent}",
                        buildResumeContent(resume)
                )
                .replace(
                        "{resumeTitle}",
                        "이력서"
                );

        return """
                You are an AI assistant for an LMS resume review feature.

                Analyze the user's resume and return ONLY valid JSON.
                Do not include markdown fences.
                Do not include explanation outside JSON.

                Rules:
                - Evaluate the resume by these sections: BASIC, EDUCATION, CAREER, SKILL.
                - BASIC means basic user profile information such as name, email, and phone.
                - EDUCATION means school, major, degree, period, and education details.
                - CAREER means work experience, projects, responsibilities, and achievements.
                - SKILL means technical skills and certificates.
                - sectionScores must contain exactly four items: BASIC, EDUCATION, CAREER, SKILL.
                - Each section score must be a number from 0 to 100.
                - overallScore must be a number from 0 to 100 and calculated from sectionScores.
                - strengths must summarize the resume's strong points.
                - weaknesses must summarize missing or weak points.
                - suggestions must provide concrete improvement suggestions.
                - improvementItems must contain concrete Korean improvement suggestions.
                - If BASIC information is not included in the resume input, evaluate only available basic fields and mention missing fields in improvementItems.
                - Return Korean text for all explanation fields.
                - BASIC is a completeness check for basic profile information, not a competency evaluation.

                Required JSON format:
                {
                  "overallScore": 78,
                  "strengths": "기술 스택과 학력 정보가 명확하게 작성되어 있습니다.",
                  "weaknesses": "경력 사항과 자격증 정보가 부족합니다.",
                  "suggestions": "경력 사항의 담당 업무를 구체적으로 작성하고, 보유 자격증을 추가하면 좋습니다.",
                  "sectionScores": [
                    {
                      "type": "BASIC",
                      "label": "기본 정보",
                      "score": 100,
                      "grade": "우수",
                      "reason": "이름, 이메일 등 기본 정보가 충분히 제공되었습니다."
                    },
                    {
                      "type": "EDUCATION",
                      "label": "학력 사항",
                      "score": 85,
                      "grade": "양호",
                      "reason": "학교와 전공 정보가 있으나 세부 활동 설명은 부족합니다."
                    },
                    {
                      "type": "CAREER",
                      "label": "경력 사항",
                      "score": 70,
                      "grade": "보통",
                      "reason": "경력 없음은 신입 기준으로 자연스럽지만 프로젝트 경험 보완이 필요합니다."
                    },
                    {
                      "type": "SKILL",
                      "label": "기술 및 자격증",
                      "score": 60,
                      "grade": "보완 필요",
                      "reason": "기술 스택은 있으나 자격증 정보가 부족합니다."
                    }
                  ],
                  "improvementItems": [
                    "경력 사항의 담당 업무를 더 구체적으로 작성해보세요.",
                    "기술 스택 태그를 3개 이상 추가하면 좋습니다.",
                    "자격증 정보가 없다면 보유 자격증을 추가해보세요."
                  ],
                  "aiResult": {
                    "summary": "전체적으로 기본 정보와 학력 정보는 양호하지만 경력 및 기술/자격증 영역 보완이 필요합니다."
                  }
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

    private String buildResumeContent(Resume resume) {
        String educationContent = resume.educations().stream()
                .map(this::formatEducation)
                .reduce(
                        (first, second) -> first + "\n" + second
                )
                .orElse("NONE");

        String careerContent = resume.careers().stream()
                .map(this::formatCareer)
                .reduce(
                        (first, second) -> first + "\n" + second
                )
                .orElse("NONE");

        return """
            Education:
            %s

            Entry level:
            %s

            Career:
            %s
            """.formatted(
                educationContent,
                resume.entryLevel(),
                careerContent
        );
    }

    private String formatEducation(
            ResumeEducation education
    ) {
        return """
            - schoolName: %s
              period: %s ~ %s
              degree: %s
              major: %s
              graduationStatus: %s
              minorOrResearch: %s
            """.formatted(
                safe(education.schoolName()),
                education.startYearMonth(),
                education.endYearMonth(),
                education.degree(),
                safe(education.major()),
                education.graduationStatus(),
                safe(education.minorOrResearch())
        ).strip();
    }

    private String formatCareer(
            ResumeCareer career
    ) {
        String endYearMonth = career.currentlyEmployed()
                ? "CURRENT"
                : career.endYearMonth().toString();

        String employmentType =
                career.employmentType().name();

        if (career.customEmploymentType() != null) {
            employmentType += " ("
                    + career.customEmploymentType()
                    + ")";
        }

        return """
            - companyName: %s
              period: %s ~ %s
              currentlyEmployed: %s
              employmentType: %s
              jobTitle: %s
            """.formatted(
                safe(career.companyName()),
                career.startYearMonth(),
                endYearMonth,
                career.currentlyEmployed(),
                employmentType,
                safe(career.jobTitle())
        ).strip();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}