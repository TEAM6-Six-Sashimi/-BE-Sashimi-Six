package com.sashimi.recommendation.domain.model;

import java.util.List;

public class JobPostingSummary {

    private final String jobRole;
    private final List<String> requiredQualifications;
    private final List<String> preferredQualifications;
    private final String experienceRequirement;
    private final String mainTaskSummary;

    public JobPostingSummary(
            String jobRole,
            List<String> requiredQualifications,
            List<String> preferredQualifications,
            String experienceRequirement,
            String mainTaskSummary
    ) {
        this.jobRole = jobRole;
        this.requiredQualifications = requiredQualifications == null ? List.of() : requiredQualifications;
        this.preferredQualifications = preferredQualifications == null ? List.of() : preferredQualifications;
        this.experienceRequirement = experienceRequirement;
        this.mainTaskSummary = mainTaskSummary;
    }

    public String jobRole() {
        return jobRole;
    }

    public List<String> requiredQualifications() {
        return requiredQualifications;
    }

    public List<String> preferredQualifications() {
        return preferredQualifications;
    }

    public String experienceRequirement() {
        return experienceRequirement;
    }

    public String mainTaskSummary() {
        return mainTaskSummary;
    }
}