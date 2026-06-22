package com.sashimi.resume.application.result;

import java.util.List;

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

    public List<SectionScoreResult> sectionScores() {
        return List.of(
                education,
                career,
                certificate
        );
    }
}