package com.sashimi.recommendation.presentation.api.response;

import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.domain.model.RecommendationAnalysisStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "채용공고 기반 추천 요약 응답")
public record JobPostingRecommendationResponse(

        @Schema(description = "채용공고 추천 ID", example = "1")
        Long recommendationId,

        @Schema(description = "AI가 분석한 직무명", example = "Frontend Developer")
        String jobTitle,

        @Schema(description = "채용공고 분석 상태", example = "COMPLETED")
        RecommendationAnalysisStatus analysisStatus,

        @Schema(description = "이력서 기반 분석 여부", example = "true")
        boolean resumeBased,

        @Schema(description = "이력서와 채용공고 요구 역량의 일치율. 이력서가 없으면 null", example = "56")
        Integer matchRate,

        @Schema(description = "추천 생성일시", example = "2026-05-27T16:30:00")
        LocalDateTime createdAt
) {
    public static JobPostingRecommendationResponse from(JobPostingRecommendation recommendation) {
        return new JobPostingRecommendationResponse(
                recommendation.recommendationId(),
                recommendation.jobTitle(),
                recommendation.analysisStatus(),
                recommendation.resumeBased(),
                recommendation.matchRate(),
                recommendation.createdAt()
        );
    }
}