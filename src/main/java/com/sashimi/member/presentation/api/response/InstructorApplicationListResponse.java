package com.sashimi.member.presentation.api.response;

import com.sashimi.member.domain.model.InstructorApplication;
import com.sashimi.user.domain.model.User;

import java.time.LocalDateTime;

public record InstructorApplicationListResponse(
        Long applicationId,
        String name,
        String loginId,
        String email,
        LocalDateTime createdAt
) {
    public static InstructorApplicationListResponse of(InstructorApplication application, User user) {
        return new InstructorApplicationListResponse(
                application.getId(),
                user.getName(),
                user.getLoginId(),
                user.getEmail(),
                application.getCreatedAt()
        );
    }
}
