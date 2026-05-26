package com.sashimi.recommendation.domain.model;

import java.util.List;

public class CertificateRecommendation {

    private final Long certificationId;
    private final String name;
    private final String reason;
    private final List<String> relatedSkills;
    private final String difficulty;

    public CertificateRecommendation(
            Long certificationId,
            String name,
            String reason,
            List<String> relatedSkills,
            String difficulty
    ) {
        this.certificationId = certificationId;
        this.name = name;
        this.reason = reason;
        this.relatedSkills = relatedSkills;
        this.difficulty = difficulty;
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
}
