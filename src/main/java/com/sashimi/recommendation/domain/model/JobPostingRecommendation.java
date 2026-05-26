package com.sashimi.recommendation.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class JobPostingRecommendation {

    private final Long recommendationId;
    private final Long userId;
    private final RecommendationInputType inputType;
    private final String sourceUrl;
    private final String rawContent;
    private final String jobTitle;
    private final RecommendationAnalysisStatus analysisStatus;
    private final boolean resumeBased;
    private final Integer matchRate;
    private final List<RequiredSkillRecommendation> requiredSkills;
    private final List<CourseRecommendation> courses;
    private final List<CertificateRecommendation> certificates;
    private final LocalDateTime createdAt;

    private JobPostingRecommendation(
            Long recommendationId,
            Long userId,
            RecommendationInputType inputType,
            String sourceUrl,
            String rawContent,
            String jobTitle,
            RecommendationAnalysisStatus analysisStatus,
            boolean resumeBased,
            Integer matchRate,
            List<RequiredSkillRecommendation> requiredSkills,
            List<CourseRecommendation> courses,
            List<CertificateRecommendation> certificates,
            LocalDateTime createdAt
    ) {
        this.recommendationId = recommendationId;
        this.userId = userId;
        this.inputType = inputType;
        this.sourceUrl = sourceUrl;
        this.rawContent = rawContent;
        this.jobTitle = jobTitle;
        this.analysisStatus = analysisStatus;
        this.resumeBased = resumeBased;
        this.matchRate = matchRate;
        this.requiredSkills = requiredSkills;
        this.courses = courses;
        this.certificates = certificates;
        this.createdAt = createdAt;
    }

    public static JobPostingRecommendation create(
            Long userId,
            RecommendationInputType inputType,
            String sourceUrl,
            String rawContent,
            boolean hasResume
    ) {
        return new JobPostingRecommendation(
                null,
                userId,
                inputType,
                sourceUrl,
                rawContent,
                null,
                RecommendationAnalysisStatus.PENDING,
                hasResume,
                null,
                List.of(),
                List.of(),
                List.of(),
                LocalDateTime.now()
        );
    }

    public JobPostingRecommendation analyzed(
            String jobTitle,
            Integer matchRate,
            List<RequiredSkillRecommendation> requiredSkills,
            List<CourseRecommendation> courses,
            List<CertificateRecommendation> certificates
    ) {
        return new JobPostingRecommendation(
                recommendationId,
                userId,
                inputType,
                sourceUrl,
                rawContent,
                jobTitle,
                RecommendationAnalysisStatus.COMPLETED,
                resumeBased,
                resumeBased ? matchRate : null,
                requiredSkills,
                courses,
                certificates,
                createdAt
        );
    }

    public JobPostingRecommendation withId(Long recommendationId) {
        return new JobPostingRecommendation(
                recommendationId,
                userId,
                inputType,
                sourceUrl,
                rawContent,
                jobTitle,
                analysisStatus,
                resumeBased,
                matchRate,
                requiredSkills,
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

    public RecommendationInputType inputType() {
        return inputType;
    }

    public String sourceUrl() {
        return sourceUrl;
    }

    public String rawContent() {
        return rawContent;
    }

    public String jobTitle() {
        return jobTitle;
    }

    public RecommendationAnalysisStatus analysisStatus() {
        return analysisStatus;
    }

    public boolean resumeBased() {
        return resumeBased;
    }

    public Integer matchRate() {
        return matchRate;
    }

    public List<RequiredSkillRecommendation> requiredSkills() {
        return requiredSkills;
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
