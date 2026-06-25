package com.sashimi.recommendation.domain.model;

import java.util.List;

public class JobFitAnalysis {

    private final FitAnalysisItem education;
    private final FitAnalysisItem career;
    private final FitAnalysisItem certification;
    private final List<String> overallComments;

    public JobFitAnalysis(
            FitAnalysisItem education,
            FitAnalysisItem career,
            FitAnalysisItem certification,
            List<String> overallComments
    ) {
        this.education = education;
        this.career = career;
        this.certification = certification;
        this.overallComments = overallComments == null ? List.of() : overallComments;
    }

    public FitAnalysisItem education() {
        return education;
    }

    public FitAnalysisItem career() {
        return career;
    }

    public FitAnalysisItem certification() {
        return certification;
    }

    public List<String> overallComments() {
        return overallComments;
    }
}