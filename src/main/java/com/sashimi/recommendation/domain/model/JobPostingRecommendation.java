package com.sashimi.recommendation.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class JobPostingRecommendation {

    private final Long recommendationId;
    private final Long userId;
    private final Long resumeId;
    private final RecommendationInputType inputType;
    private final String sourceUrl;
    private final String rawContent;
    private final String resumeContent;
    private final RecommendationAnalysisStatus analysisStatus;
    private final boolean resumeBased;
    private final JobPostingSummary summary;
    private final JobFitAnalysis fitAnalysis;
    private final List<CourseRecommendation> courses;
    private final List<CertificateRecommendation> certificates;
    private final LocalDateTime createdAt;

    private JobPostingRecommendation(
            Long recommendationId,
            Long userId,
            Long resumeId,
            RecommendationInputType inputType,
            String sourceUrl,
            String rawContent,
            String resumeContent,
            RecommendationAnalysisStatus analysisStatus,
            boolean resumeBased,
            JobPostingSummary summary,
            JobFitAnalysis fitAnalysis,
            List<CourseRecommendation> courses,
            List<CertificateRecommendation> certificates,
            LocalDateTime createdAt
    ) {
        this.recommendationId = recommendationId;
        this.userId = userId;
        this.resumeId = resumeId;
        this.inputType = inputType;
        this.sourceUrl = sourceUrl;
        this.rawContent = rawContent;
        this.resumeContent = resumeContent;
        this.analysisStatus = analysisStatus;
        this.resumeBased = resumeBased;
        this.summary = summary;
        this.fitAnalysis = fitAnalysis;
        this.courses = courses == null ? List.of() : courses;
        this.certificates = certificates == null ? List.of() : certificates;
        this.createdAt = createdAt;
    }

    public static JobPostingRecommendation create(
            Long userId,
            Long resumeId,
            RecommendationInputType inputType,
            String sourceUrl,
            String rawContent,
            String resumeContent,
            boolean hasResume
    ) {
        return new JobPostingRecommendation(
                null,
                userId,
                resumeId,
                inputType,
                sourceUrl,
                rawContent,
                resumeContent,
                RecommendationAnalysisStatus.PENDING,
                hasResume,
                null,
                null,
                List.of(),
                List.of(),
                LocalDateTime.now()
        );
    }

    public static JobPostingRecommendation restore(
            Long recommendationId,
            Long userId,
            Long resumeId,
            RecommendationInputType inputType,
            String sourceUrl,
            String rawContent,
            String resumeContent,
            RecommendationAnalysisStatus analysisStatus,
            boolean resumeBased,
            JobPostingSummary summary,
            JobFitAnalysis fitAnalysis,
            List<CourseRecommendation> courses,
            List<CertificateRecommendation> certificates,
            LocalDateTime createdAt
    ) {
        return new JobPostingRecommendation(
                recommendationId,
                userId,
                resumeId,
                inputType,
                sourceUrl,
                rawContent,
                resumeContent,
                analysisStatus,
                resumeBased,
                summary,
                fitAnalysis,
                courses,
                certificates,
                createdAt
        );
    }

    public JobPostingRecommendation analyzed(
            JobPostingSummary summary,
            JobFitAnalysis fitAnalysis,
            List<CourseRecommendation> courses,
            List<CertificateRecommendation> certificates
    ) {
        return new JobPostingRecommendation(
                recommendationId,
                userId,
                resumeId,
                inputType,
                sourceUrl,
                rawContent,
                resumeContent,
                RecommendationAnalysisStatus.COMPLETED,
                resumeBased,
                summary,
                fitAnalysis,
                courses,
                certificates,
                createdAt
        );
    }

    public JobPostingRecommendation failed() {
        return new JobPostingRecommendation(
                recommendationId,
                userId,
                resumeId,
                inputType,
                sourceUrl,
                rawContent,
                resumeContent,
                RecommendationAnalysisStatus.FAILED,
                resumeBased,
                summary,
                fitAnalysis,
                courses,
                certificates,
                createdAt
        );
    }

    public JobPostingRecommendation withId(Long recommendationId) {
        return new JobPostingRecommendation(
                recommendationId,
                userId,
                resumeId,
                inputType,
                sourceUrl,
                rawContent,
                resumeContent,
                analysisStatus,
                resumeBased,
                summary,
                fitAnalysis,
                courses,
                certificates,
                createdAt
        );
    }

    public Long recommendationId() {
        return recommendationId;
    }

    public Long userId() {
        return userId;
    }

    public Long resumeId() {
        return resumeId;
    }

    public RecommendationInputType inputType() {
        return inputType;
    }

    public String sourceUrl() {
        return sourceUrl;
    }

    public String rawContent() {
        return rawContent;
    }

    public String resumeContent() {
        return resumeContent;
    }

    public RecommendationAnalysisStatus analysisStatus() {
        return analysisStatus;
    }

    public boolean resumeBased() {
        return resumeBased;
    }

    public JobPostingSummary summary() {
        return summary;
    }

    public JobFitAnalysis fitAnalysis() {
        return fitAnalysis;
    }

    public List<CourseRecommendation> courses() {
        return courses;
    }

    public List<CertificateRecommendation> certificates() {
        return certificates;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }
}