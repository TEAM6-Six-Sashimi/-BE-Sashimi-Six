package com.sashimi.review.presentation.api.request;

import com.sashimi.review.domain.model.ReviewReportCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportReviewRequest(
        @NotNull(message = "신고 카테고리를 선택해주세요.")
        ReviewReportCategory category,

        @Size(max = 200, message = "신고 사유는 200자 이내로 입력해주세요.")
        String reason
) {}
