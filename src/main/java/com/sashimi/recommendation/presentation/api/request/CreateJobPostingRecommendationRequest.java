package com.sashimi.recommendation.presentation.api.request;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.recommendation.application.command.CreateJobPostingRecommendationCommand;
import com.sashimi.recommendation.domain.model.RecommendationInputType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채용공고 기반 추천 생성 요청")
public record CreateJobPostingRecommendationRequest(

        @Schema(description = "비교할 이력서 ID. 없으면 이력서 기반 적합도 분석을 생략합니다.", example = "1")
        Long resumeId,

        @Schema(description = "입력 방식", example = "TEXT")
        RecommendationInputType inputType,

        @Schema(description = "채용공고 주소(URL). 입력 타입이 URL인 경우 사용", example = "https://example.com/jobs/frontend")
        String sourceUrl,

        @Schema(description = "채용공고 원문 텍스트. inputType이 TEXT인 경우 사용",
                example = "프론트엔드 개발자 채용공고입니다. React, TypeScript, Next.js 경험을 우대합니다.")
        String rawContent
) {

        private static final int MIN_RAW_CONTENT_LENGTH = 50;

        public CreateJobPostingRecommendationCommand toCommand(Long userId) {
                validate();

                return new CreateJobPostingRecommendationCommand(
                        userId,
                        resumeId,
                        inputType,
                        normalizedSourceUrl(),
                        normalizedRawContent()
                );
        }

        private void validate() {
                if (inputType == null) {
                        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
                }

                if (inputType == RecommendationInputType.URL) {
                        validateUrlInput();
                        return;
                }

                if (inputType == RecommendationInputType.TEXT) {
                        validateTextInput();
                }
        }

        private void validateUrlInput() {
                if (sourceUrl == null || sourceUrl.isBlank()) {
                        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
                }
        }

        private void validateTextInput() {
                if (rawContent == null || rawContent.isBlank()) {
                        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
                }

                if (rawContent.trim().length() < MIN_RAW_CONTENT_LENGTH) {
                        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
                }
        }

        private String normalizedSourceUrl() {
                if (sourceUrl == null) {
                        return null;
                }

                return sourceUrl.trim();
        }

        private String normalizedRawContent() {
                if (rawContent == null) {
                        return null;
                }

                return rawContent.trim();
        }
}