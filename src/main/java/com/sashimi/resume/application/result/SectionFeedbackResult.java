package com.sashimi.resume.application.result;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.domain.model.ResumeReviewSection;

// 각 평가 항목의 강점 또는 보완점 메시지
public record SectionFeedbackResult(
        ResumeReviewSection section,
        String label,
        ResumeFeedbackType type,
        String message
) {

    public SectionFeedbackResult {
        if (section == null || type == null) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
            );
        }

        if (label == null || label.isBlank()) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
            );
        }

        if (!label.equals(section.label())) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
            );
        }

        if (message == null || message.isBlank()) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
            );
        }
    }

    public static SectionFeedbackResult strength(
            SectionScoreResult sectionScore
    ) {
        if (sectionScore.score() < 80) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
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
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
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