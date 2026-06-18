package com.sashimi.member.presentation.api.response;

import com.sashimi.member.domain.model.ApprovalStatus;
import com.sashimi.member.domain.model.InstructorApplication;
import com.sashimi.member.domain.model.RejectionCategory;
import com.sashimi.user.domain.model.User;

import java.time.LocalDateTime;

public record MyInstructorApplicationDetailResponse(
        String userName,
        Long categoryId,
        LocalDateTime createdAt,
        ApprovalStatus approvalStatus,
        RejectionCategory rejectionCategory,
        String rejectionReason,
        LocalDateTime rejectedAt
) {
    public static MyInstructorApplicationDetailResponse of(InstructorApplication application, User user) {
        return new MyInstructorApplicationDetailResponse(
                user.getName(),
                application.getCategoryId(),
                application.getCreatedAt(),
                application.getApprovalStatus(),
                application.getRejectionCategory(),
                application.getRejectionReason(),
                application.getUpdatedAt()
        );
    }
}
