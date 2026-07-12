package com.sashimi.ai.domain.model;

public enum AiFeatureType {

    RESUME_REVIEW("이력서 AI 평가"),
    COVER_LETTER_REVIEW("자기소개서 AI 평가"),
    JOB_POSTING_ANALYSIS("채용공고 분석");

    private final String label;

    AiFeatureType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}