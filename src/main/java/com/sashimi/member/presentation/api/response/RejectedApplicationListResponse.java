package com.sashimi.member.presentation.api.response;

import com.sashimi.member.domain.model.InstructorApplication;
import com.sashimi.member.domain.model.RejectionCategory;
import com.sashimi.user.domain.model.User;

import java.time.LocalDateTime;

public record RejectedApplicationListResponse(
        Long applicationId,
        String name,
        String loginId,
        String email,
        LocalDateTime rejectedAt,
        RejectionCategory rejectionCategory,
        String rejectionReason
) {
    public static RejectedApplicationListResponse of(InstructorApplication application, User user) {
        return new RejectedApplicationListResponse(
                application.getId(),
                user.getName(),
                user.getLoginId(),
                user.getEmail(),
                application.getUpdatedAt(),
                application.getRejectionCategory(),
                application.getRejectionReason()
        );
    }
}
