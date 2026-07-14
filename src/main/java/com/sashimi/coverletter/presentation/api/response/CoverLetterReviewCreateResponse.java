package com.sashimi.coverletter.presentation.api.response;

import java.time.LocalDateTime;

public record CoverLetterReviewCreateResponse(
        Long reviewId,
        LocalDateTime createdAt,
        CoverLetterReviewSummaryResponse summary
) {
}