package com.sashimi.resume.application.result;

import com.sashimi.resume.domain.model.ResumeReviewSection;

public record SectionScoreResult(
        ResumeReviewSection type,
        String label,
        int score,
        String grade,
        String reason
) {

    public static SectionScoreResult calculated(
            ResumeReviewSection type,
            int score,
            String grade
    ) {
        return new SectionScoreResult(
                type,
                type.label(),
                score,
                grade,
                null
        );
    }

    public SectionScoreResult withReason(String reason) {
        return new SectionScoreResult(
                this.type,
                this.label,
                this.score,
                this.grade,
                reason
        );
    }
}