package com.sashimi.recommendation.presentation.api.response;

import com.sashimi.recommendation.domain.model.JobFitAnalysis;

import java.util.List;

public record JobFitAnalysisResponse(
        FitAnalysisItemResponse education,
        FitAnalysisItemResponse career,
        FitAnalysisItemResponse certification,
        List<String> overallComments
) {
    public static JobFitAnalysisResponse from(JobFitAnalysis analysis) {
        if (analysis == null) {
            return null;
        }

        return new JobFitAnalysisResponse(
                FitAnalysisItemResponse.from(analysis.education()),
                FitAnalysisItemResponse.from(analysis.career()),
                FitAnalysisItemResponse.from(analysis.certification()),
                analysis.overallComments()
        );
    }
}