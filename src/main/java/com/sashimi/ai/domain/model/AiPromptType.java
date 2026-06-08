package com.sashimi.ai.domain.model;

public enum AiPromptType {

    // 사용자의 이력서 정보를 기반으로 이력서 초안을 생성할 때 사용

    RESUME_GENERATE,
    // 사용자가 작성한 이력서를 평가하고 강점/약점/개선점을 제공할 때 사용
    RESUME_REVIEW,

    // AI 평가 결과를 바탕으로 개선된 이력서 내용을 생성할 때
    RESUME_IMPROVE,

    // 업로드한 자격증 파일의 진위 여부를 검증할 때 사용
    CERTIFICATE_VERIFY,

    // 채용 공고 기반 분석할 때 사용
    JOB_POSTING_ANALYSIS
}
