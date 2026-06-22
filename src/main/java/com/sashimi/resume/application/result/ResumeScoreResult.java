package com.sashimi.resume.application.result;

import java.util.List;
import java.util.Objects;

public record ResumeScoreResult(
        SectionScoreResult education,
        SectionScoreResult career,
        SectionScoreResult certificate,
        int careerCountScore,
        int careerPeriodScore,
        int careerContinuityScore,
        int overallScore,
        String overallGrade
) {

    public ResumeScoreResult {
        Objects.requireNonNull(
                education,
                "학력 평가 결과는 필수입니다."
        );
        Objects.requireNonNull(
                career,
                "경력 평가 결과는 필수입니다."
        );
        Objects.requireNonNull(
                certificate,
                "자격증 평가 결과는 필수입니다."
        );

        if (overallScore < 0 || overallScore > 100) {
            throw new IllegalArgumentException(
                    "전체 점수는 0점 이상 100점 이하여야 합니다."
            );
        }
    }

    public List<SectionScoreResult> sectionScores() {
        return List.of(
                education,
                career,
                certificate
        );
    }
}