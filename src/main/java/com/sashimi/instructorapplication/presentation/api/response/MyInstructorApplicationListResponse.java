package com.sashimi.instructorapplication.presentation.api.response;

import com.sashimi.instructorapplication.domain.model.ApprovalStatus;
import com.sashimi.instructorapplication.domain.model.InstructorApplication;

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
