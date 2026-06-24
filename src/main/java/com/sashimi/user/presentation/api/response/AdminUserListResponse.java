package com.sashimi.user.presentation.api.response;

import com.sashimi.user.domain.model.User;

import java.time.LocalDateTime;

public record AdminUserListResponse(
        Long id,
        String name,
        String loginId,
        String email,
        String role,
        LocalDateTime createdAt,
        String status,
        LocalDateTime lastLoginAt
) {
    public static AdminUserListResponse from(User user) {
        return new AdminUserListResponse(
                user.getId(),
                user.getName(),
                user.getLoginId(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt(),
                user.getStatus().name(),
                user.getLastLoginAt()
        );
    }
}
