package com.sashimi.resume.domain.model;

public enum ResumeReviewSection {

    EDUCATION("학력 사항"),
    CAREER("경력 사항"),
    CERTIFICATE("자격증 사항");

    private final String label;

    ResumeReviewSection(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}