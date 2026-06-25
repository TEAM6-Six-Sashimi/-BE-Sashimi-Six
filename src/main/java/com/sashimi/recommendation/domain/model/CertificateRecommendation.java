package com.sashimi.recommendation.domain.model;

import java.time.LocalDate;
import java.util.List;

public class CertificateRecommendation {

    private final Long certificationId;
    private final String name;
    private final String reason;
    private final List<String> relatedSkills;
    private final String difficulty;
    private final LocalDate nextExamDate;
    private final LocalDate applicationStartDate;
    private final LocalDate applicationEndDate;

    public CertificateRecommendation(
            Long certificationId,
            String name,
            String reason,
            List<String> relatedSkills,
            String difficulty
    ) {
        this(
                certificationId,
                name,
                reason,
                relatedSkills,
                difficulty,
                null,
                null,
                null
        );
    }

    public CertificateRecommendation(
            Long certificationId,
            String name,
            String reason,
            List<String> relatedSkills,
            String difficulty,
            LocalDate nextExamDate,
            LocalDate applicationStartDate,
            LocalDate applicationEndDate
    ) {
        this.certificationId = certificationId;
        this.name = name;
        this.reason = reason;
        this.relatedSkills = relatedSkills;
        this.difficulty = difficulty;
        this.nextExamDate = nextExamDate;
        this.applicationStartDate = applicationStartDate;
        this.applicationEndDate = applicationEndDate;
    }

    public Long certificationId() {
        return certificationId;
    }

    public String name() {
        return name;
    }

    public String reason() {
        return reason;
    }

    public List<String> relatedSkills() {
        return relatedSkills;
    }

    public String difficulty() {
        return difficulty;
    }

    public LocalDate nextExamDate() {
        return nextExamDate;
    }

    public LocalDate applicationStartDate() {
        return applicationStartDate;
    }

    public LocalDate applicationEndDate() {
        return applicationEndDate;
    }
}