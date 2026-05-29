package com.sashimi.recommendation.presentation.api.response;

import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.domain.model.RecommendationAnalysisStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "채용공고 기반 추천 결과 응답")
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

        @ArraySchema(
                schema = @Schema(implementation = RequiredSkillRecommendationResponse.class),
                arraySchema = @Schema(description = "채용공고에서 추출된 요구 역량 목록")
        )
        List<RequiredSkillRecommendationResponse> requiredSkills,

        @ArraySchema(
                schema = @Schema(implementation = CertificateRecommendationResponse.class),
                arraySchema = @Schema(description = "채용공고 기반 추천 자격증 목록")
        )
        List<CertificateRecommendationResponse> certificates,

        @ArraySchema(
                schema = @Schema(implementation = CourseRecommendationResponse.class),
                arraySchema = @Schema(description = "역량 보완을 위한 추천 강의 목록")
        )
        List<CourseRecommendationResponse> courses,

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
                recommendation.requiredSkills().stream()
                        .map(RequiredSkillRecommendationResponse::from)
                        .toList(),
                recommendation.certificates().stream()
                        .map(CertificateRecommendationResponse::from)
                        .toList(),
                recommendation.courses().stream()
                        .map(CourseRecommendationResponse::from)
                        .toList(),
                recommendation.createdAt()
        );
    }
}