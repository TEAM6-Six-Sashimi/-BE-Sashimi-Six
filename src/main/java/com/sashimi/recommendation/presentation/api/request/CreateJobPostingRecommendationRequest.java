package com.sashimi.recommendation.presentation.api.request;

import com.sashimi.recommendation.domain.model.RecommendationInputType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채용공고 기반 추천 생성 요청")
public record CreateJobPostingRecommendationRequest(

        @Schema(description = "비교할 이력서 ID. 없으면 이력서 기반 적합도 분석을 생략합니다.", example = "1")
        Long resumeId,

        @Schema(description = "입력 타입", example = "TEXT")
        RecommendationInputType inputType,

        @Schema(description = "채용공고 URL. inputType이 URL인 경우 사용", example = "https://example.com/jobs/frontend")
        String sourceUrl,

        @Schema(description = "채용공고 원문 텍스트. inputType이 TEXT인 경우 사용",
                example = "프론트엔드 개발자 채용공고입니다. React, TypeScript, Next.js 경험을 우대합니다.")
        String rawContent
) {
}