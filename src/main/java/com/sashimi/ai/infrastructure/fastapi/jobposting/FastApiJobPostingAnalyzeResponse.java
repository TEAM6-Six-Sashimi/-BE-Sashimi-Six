package com.sashimi.ai.infrastructure.fastapi.jobposting;

import java.util.List;

public record FastApiJobPostingAnalyzeResponse(
        Summary summary,
        FitAnalysis fitAnalysis,
        List<Certificate> certificates,
        List<Course> courses
) {
    public record Summary(
            String jobRole,
            List<String> requiredQualifications,
            List<String> preferredQualifications,
            String experienceRequirement,
            String mainTaskSummary
    ) {
    }

    public record FitAnalysis(
            FitAnalysisItem education,
            FitAnalysisItem career,
            FitAnalysisItem certification,
            List<String> overallComments
    ) {
    }

    public record FitAnalysisItem(
            String status,
            String required,
            String user,
            String comment,
            List<String> missingItems
    ) {
    }

    public record Certificate(
            Long certificationId,
            String name,
            String reason,
            List<String> relatedSkills,
            String difficulty
    ) {
    }

    public record Course(
            Long courseId,
            String title,
            String instructor,
            String matchedSkill,
            String reason
    ) {
    }
}