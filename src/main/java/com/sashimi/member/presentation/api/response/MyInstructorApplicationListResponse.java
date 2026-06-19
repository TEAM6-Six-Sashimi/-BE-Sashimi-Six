package com.sashimi.member.presentation.api.response;

import com.sashimi.member.domain.model.ApprovalStatus;
import com.sashimi.member.domain.model.InstructorApplication;

import java.time.LocalDateTime;

public record MyInstructorApplicationListResponse(
        Long applicationId,
        Long categoryId,
        LocalDateTime createdAt,
        ApprovalStatus approvalStatus
) {
    public static MyInstructorApplicationListResponse from(InstructorApplication application) {
        return new MyInstructorApplicationListResponse(
                application.getId(),
                application.getCategoryId(),
                application.getCreatedAt(),
                application.getApprovalStatus()
        );
    }
}
