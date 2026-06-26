package com.sashimi.recommendation.presentation.api.response;

import com.sashimi.recommendation.domain.model.JobPostingSummary;

import java.util.List;

public record JobPostingSummaryResponse(
        String jobRole,
        List<String> requiredQualifications,
        List<String> preferredQualifications,
        String experienceRequirement,
        String mainTaskSummary
) {
    public static JobPostingSummaryResponse from(JobPostingSummary summary) {
        if (summary == null) {
            return null;
        }

        return new JobPostingSummaryResponse(
                summary.jobRole(),
                summary.requiredQualifications(),
                summary.preferredQualifications(),
                summary.experienceRequirement(),
                summary.mainTaskSummary()
        );
    }
}