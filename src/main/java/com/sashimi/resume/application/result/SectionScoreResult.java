package com.sashimi.resume.application.result;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.domain.model.ResumeReviewSection;

// 이력서 각 항목의 점수와 등급 결과
public record SectionScoreResult(
        ResumeReviewSection type,
        String label,
        int score,
        String grade
) {

    public SectionScoreResult {
        if (type == null) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        if (score < 0 || score > 100) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        if (label == null || label.isBlank()) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        if (!label.equals(type.label())) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        if (grade == null || grade.isBlank()) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }
    }

    public static SectionScoreResult of(
            ResumeReviewSection type,
            int score,
            String grade
    ) {
        return new SectionScoreResult(
                type,
                type.label(),
                score,
                grade
        );
    }
}