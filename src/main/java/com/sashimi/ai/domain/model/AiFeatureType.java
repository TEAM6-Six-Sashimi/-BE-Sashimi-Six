package com.sashimi.ai.domain.model;

import com.sashimi.ai.metric.AiMetrics;

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

    public static AiFeatureType fromMetricFeature(String feature) {
        if (AiMetrics.FEATURE_RESUME_REVIEW.equals(feature)) {
            return RESUME_REVIEW;
        }

        if (AiMetrics.FEATURE_JOB_POSTING_RECOMMENDATION.equals(feature)) {
            return JOB_POSTING_ANALYSIS;
        }

        if (AiMetrics.FEATURE_COVER_LETTER_REVIEW.equals(feature)) {
            return COVER_LETTER_REVIEW;
        }

        throw new IllegalArgumentException("지원하지 않는 AI 기능입니다. feature=" + feature);
    }
}