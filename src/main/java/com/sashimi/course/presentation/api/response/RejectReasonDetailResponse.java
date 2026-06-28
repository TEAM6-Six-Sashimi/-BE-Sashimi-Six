package com.sashimi.course.presentation.api.response;

import com.sashimi.course.application.query.RejectReasonView;

import java.time.LocalDateTime;

public record RejectReasonDetailResponse(
        Long courseId,
        String title,
        LocalDateTime rejectedAt,
        RejectReasonResponse category,
        String detail
) {
    public static RejectReasonDetailResponse from(RejectReasonView view) {
        RejectReasonResponse category = view.category() == null
                ? null
                : RejectReasonResponse.from(view.category());
        return new RejectReasonDetailResponse(
                view.courseId(),
                view.title(),
                view.rejectedAt(),
                category,
                view.detail()
        );
    }
}
