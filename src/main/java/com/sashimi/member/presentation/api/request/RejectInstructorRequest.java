package com.sashimi.member.presentation.api.request;

import com.sashimi.member.domain.model.RejectionCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RejectInstructorRequest(
        @NotNull(message = "반려 사유 카테고리는 필수입니다.")
        RejectionCategory rejectionCategory,

        @NotBlank(message = "반려 사유 상세 내용은 필수입니다.")
        @Size(max = 100, message = "반려 사유 상세 내용은 100자 이내로 입력해주세요.")
        String rejectionReason
) {}
