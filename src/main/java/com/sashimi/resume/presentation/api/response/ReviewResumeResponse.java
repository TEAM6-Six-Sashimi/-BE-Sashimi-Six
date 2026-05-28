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

        @Schema(description = "종합 점수", example = "78")
        BigDecimal overallScore,

        @Schema(description = "강점", example = "기술 스택과 학력 정보가 명확하게 작성되어 있습니다.")
        String strengths,

        @Schema(description = "약점", example = "경력 사항과 자격증 정보가 부족합니다.")
        String weaknesses,

        @Schema(description = "개선 제안", example = "경력 사항의 담당 업무를 구체적으로 작성하고, 보유 자격증을 추가하면 좋습니다.")
        String suggestions,

        @Schema(
                description = "AI 상세 평가 결과 JSON. sectionScores와 improvementItems를 포함합니다.",
                example = "{\"overallScore\":78,\"sectionScores\":[{\"type\":\"BASIC\",\"label\":\"기본 정보\",\"score\":100,\"grade\":\"우수\"}],\"improvementItems\":[\"자격증 정보를 추가해보세요.\"]}"
        )
        String aiResult,

        @Schema(description = "평가 일시", example = "2026-05-27T16:30:00")
        LocalDateTime evaluationAt
) {

    public static ReviewResumeResponse from(ResumeEvaluation evaluation) {
        return new ReviewResumeResponse(
                evaluation.evaluationId(),
                evaluation.overallScore(),
                evaluation.strengths(),
                evaluation.weaknesses(),
                evaluation.suggestions(),
                evaluation.aiResult(),
                evaluation.evaluationAt()
        );
    }
}