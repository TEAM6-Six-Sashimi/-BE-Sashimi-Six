package com.sashimi.ai.domain.model;

public enum AiPromptType {

    RESUME_GENERATE,

    RESUME_IMPROVE,

    // 경력 2개 이상의 업무 연속성 판단
    RESUME_CAREER_CONTINUITY,

    CERTIFICATE_VERIFY,

    JOB_POSTING_ANALYSIS
}