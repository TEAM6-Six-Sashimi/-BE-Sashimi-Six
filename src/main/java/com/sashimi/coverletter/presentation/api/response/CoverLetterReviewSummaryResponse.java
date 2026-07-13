package com.sashimi.coverletter.presentation.api.response;

public record CoverLetterReviewSummaryResponse(
        int completedCount,
        int totalCount,
        int needRevisionCount,
        int recommendedCount,
        int spellingCorrectionCount,
        int repeatedExpressionCount,
        int averageSentenceLength,
        String overallComment
) {
}