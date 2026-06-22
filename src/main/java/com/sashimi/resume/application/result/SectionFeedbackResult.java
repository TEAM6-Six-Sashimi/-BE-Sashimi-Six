package com.sashimi.resume.application.result;

import com.sashimi.resume.domain.model.ResumeReviewSection;

public record SectionFeedbackResult(
        ResumeReviewSection section,
        String label,
        ResumeFeedbackType type,
        String message
) {

    public static SectionFeedbackResult strength(
            SectionScoreResult sectionScore
    ) {
        if (sectionScore.score() < 80) {
            throw new IllegalArgumentException(
                    "80점 미만 영역은 강점으로 생성할 수 없습니다."
            );
        }

        String message = "%s이 %s하게 구성되어 있습니다."
                .formatted(
                        sectionScore.label(),
                        sectionScore.grade()
                );

        return new SectionFeedbackResult(
                sectionScore.type(),
                sectionScore.label(),
                ResumeFeedbackType.STRENGTH,
                message
        );
    }

    public static SectionFeedbackResult improvement(
            SectionScoreResult sectionScore,
            String message
    ) {
        if (sectionScore.score() >= 80) {
            throw new IllegalArgumentException(
                    "80점 이상 영역은 보완점으로 생성할 수 없습니다."
            );
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException(
                    "보완 내용은 필수입니다."
            );
        }

        return new SectionFeedbackResult(
                sectionScore.type(),
                sectionScore.label(),
                ResumeFeedbackType.IMPROVEMENT,
                message
        );
    }
}