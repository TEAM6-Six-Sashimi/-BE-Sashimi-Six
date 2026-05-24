package com.sashimi.resume.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * AI 이력서 평가 요청 DTO.
 *
 * jobPostingId는 선택값이다.
 * 특정 채용공고와 비교 평가하고 싶을 때만 전달한다.
 */
@Schema(description = "AI 이력서 평가 요청")
public record ReviewResumeRequest(

        @Schema(
                description = "비교 평가할 채용공고 ID. 일반 평가인 경우 null 가능",
                example = "1",
                nullable = true
        )
        Long jobPostingId
) {
}
