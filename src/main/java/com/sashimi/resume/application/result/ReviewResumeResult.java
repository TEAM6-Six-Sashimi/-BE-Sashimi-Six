package com.sashimi.resume.application.result;

import com.sashimi.resume.domain.model.ResumeReviewSection;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public record ReviewResumeResult(
        ResumeScoreResult scoreResult,
        List<SectionFeedbackResult> feedbacks
) {

    public ReviewResumeResult {
        Objects.requireNonNull(
                scoreResult,
                "점수 결과는 필수입니다."
        );

        Objects.requireNonNull(
                feedbacks,
                "영역별 피드백은 필수입니다."
        );

        feedbacks = List.copyOf(feedbacks);

        if (feedbacks.size() != 3) {
            throw new IllegalArgumentException(
                    "학력, 경력, 자격증 피드백이 각각 하나씩 필요합니다."
            );
        }

        Set<ResumeReviewSection> sections =
                EnumSet.noneOf(ResumeReviewSection.class);

        for (SectionFeedbackResult feedback : feedbacks) {
            if (!sections.add(feedback.section())) {
                throw new IllegalArgumentException(
                        "동일한 평가 영역의 피드백이 중복되었습니다."
                );
            }
        }

        if (!sections.equals(
                EnumSet.allOf(ResumeReviewSection.class)
        )) {
            throw new IllegalArgumentException(
                    "학력, 경력, 자격증 피드백이 모두 필요합니다."
            );
        }
    }
}