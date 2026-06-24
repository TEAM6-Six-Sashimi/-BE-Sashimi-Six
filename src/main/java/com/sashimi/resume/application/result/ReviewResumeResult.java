package com.sashimi.resume.application.result;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.domain.model.ResumeReviewSection;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

// 최종 이력서 평과 결고
// 점수 결과와 피드백을 조립해서 컨트롤러 응답으로 전달
public record ReviewResumeResult(
        ResumeScoreResult scoreResult,
        List<SectionFeedbackResult> feedbacks
) {

    public ReviewResumeResult {
        if (scoreResult == null || feedbacks == null) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
            );
        }

        feedbacks = List.copyOf(feedbacks);

        if (feedbacks.size() != 3) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
            );
        }

        Set<ResumeReviewSection> sections =
                EnumSet.noneOf(ResumeReviewSection.class);

        for (SectionFeedbackResult feedback : feedbacks) {
            if (feedback == null) {
                throw new BusinessException(
                        ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
                );
            }

            if (!sections.add(feedback.section())) {
                throw new BusinessException(
                        ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
                );
            }
        }

        if (!sections.equals(
                EnumSet.allOf(ResumeReviewSection.class)
        )) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
            );
        }
    }
}