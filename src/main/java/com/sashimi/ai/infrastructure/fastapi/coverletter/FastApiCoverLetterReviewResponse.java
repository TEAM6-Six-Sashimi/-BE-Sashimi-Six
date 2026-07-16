package com.sashimi.ai.infrastructure.fastapi.coverletter;

import java.util.List;

public record FastApiCoverLetterReviewResponse(
        String overallComment,
        int repeatedExpressionCount,
        int averageSentenceLength,
        List<QuestionReview> questions
) {
    public record QuestionReview(
            String questionKey,
            String status,
            String summaryFeedback,
            List<SpellingCorrection> spellingCorrections,
            List<RepeatedExpression> repeatedExpressions,
            int expressionImprovementCount,
            int flowImprovementCount,
            String feedback,
            String improvedExample
    ) {
    }

    public record SpellingCorrection(
            String original,
            String corrected
    ) {
    }

    public record RepeatedExpression(
            String expression,
            int count
    ) {
    }
}