package com.sashimi.coverletter.presentation.api.response;

import java.time.LocalDateTime;
import java.util.List;

public record CoverLetterReviewResultResponse(
        Long reviewId,
        LocalDateTime createdAt,
        CoverLetterReviewSummaryResponse summary,
        List<CoverLetterReviewQuestionResponse> questions
) {
}