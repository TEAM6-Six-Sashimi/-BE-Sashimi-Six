package com.sashimi.resume.presentation.api.response;

import com.sashimi.resume.domain.model.ResumeEvaluation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI 이력서 평가 API 응답 DTO.
 *
 * domain model인 ResumeEvaluation을
 * 클라이언트에게 내려줄 응답 형태로 변환한다.
 */
@Schema(description = "AI 이력서 평가 응답")
public record ReviewResumeResponse(

        @Schema(description = "평가 결과 ID", example = "10")
        Long evaluationId,

        @Schema(description = "종합 점수", example = "82.5")
        BigDecimal overallScore,

        @Schema(description = "강점", example = "Java/Spring 기반 프로젝트 경험이 강점입니다.")
        String strengths,

        @Schema(description = "약점", example = "클라우드 배포와 운영 경험이 부족합니다.")
        String weaknesses,

        @Schema(description = "개선 제안", example = "AWS 배포 경험과 Redis 캐싱 프로젝트를 보완하면 좋습니다.")
        String suggestions,

        @Schema(description = "평가 일시", example = "2026-05-22T10:30:00")
        LocalDateTime evaluationAt
) {

    public static ReviewResumeResponse from(ResumeEvaluation evaluation) {
        return new ReviewResumeResponse(
                evaluation.evaluationId(),
                evaluation.overallScore(),
                evaluation.strengths(),
                evaluation.weaknesses(),
                evaluation.suggestions(),
                evaluation.evaluationAt()
        );
    }
}
