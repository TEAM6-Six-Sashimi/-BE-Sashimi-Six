package com.sashimi.recommendation.application.port;

import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import com.sashimi.recommendation.domain.model.CourseRecommendation;
import com.sashimi.recommendation.domain.model.JobFitAnalysis;
import com.sashimi.recommendation.domain.model.JobPostingSummary;

import java.util.List;

public record JobPostingRecommendationAnalyzeResult(
        JobPostingSummary summary,
        JobFitAnalysis fitAnalysis,
        List<CertificateRecommendation> certificates,
        List<CourseRecommendation> courses
) {
    public JobPostingRecommendationAnalyzeResult {
        certificates = certificates == null ? List.of() : certificates;
        courses = courses == null ? List.of() : courses;
    }
}