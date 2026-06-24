package com.sashimi.resume.presentation.api.response;

import com.sashimi.resume.application.result.ReviewResumeResult;
import com.sashimi.resume.application.result.SectionFeedbackResult;
import com.sashimi.resume.application.result.SectionScoreResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "AI 이력서 평가 응답")
public record ReviewResumeResponse(

        @Schema(description = "전체 점수", example = "78")
        int overallScore,

        @Schema(description = "전체 등급", example = "보통")
        String overallGrade,

        @Schema(description = "항목별 평가 점수")
        List<SectionScoreResponse> sectionScores,

        @Schema(description = "항목별 강점 또는 보완점")
        List<SectionFeedbackResponse> feedbacks
) {

    public static ReviewResumeResponse from(
            ReviewResumeResult result
    ) {
        return new ReviewResumeResponse(
                result.scoreResult().overallScore(),
                result.scoreResult().overallGrade(),
                result.scoreResult()
                        .sectionScores()
                        .stream()
                        .map(SectionScoreResponse::from)
                        .toList(),
                result.feedbacks()
                        .stream()
                        .map(SectionFeedbackResponse::from)
                        .toList()
        );
    }

    public record SectionScoreResponse(
            @Schema(description = "평가 항목", example = "EDUCATION")
            String type,

            @Schema(description = "평가 항목명", example = "학력 사항")
            String label,

            @Schema(description = "점수", example = "85")
            int score,

            @Schema(description = "등급", example = "양호")
            String grade
    ) {

        public static SectionScoreResponse from(
                SectionScoreResult result
        ) {
            return new SectionScoreResponse(
                    result.type().name(),
                    result.label(),
                    result.score(),
                    result.grade()
            );
        }
    }

    public record SectionFeedbackResponse(
            @Schema(description = "평가 항목", example = "CAREER")
            String section,

            @Schema(description = "평가 항목명", example = "경력 사항")
            String label,

            @Schema(description = "피드백 유형", example = "IMPROVEMENT")
            String type,

            @Schema(description = "피드백 내용", example = "경력 사항을 추가하면 이력서 완성도를 높일 수 있습니다.")
            String message
    ) {

        public static SectionFeedbackResponse from(
                SectionFeedbackResult result
        ) {
            return new SectionFeedbackResponse(
                    result.section().name(),
                    result.label(),
                    result.type().name(),
                    result.message()
            );
        }
    }
}