package com.sashimi.user.presentation.api.response;

import com.sashimi.user.domain.model.User;

import java.time.LocalDateTime;

public record AdminUserDetailResponse(
        String name,
        String loginId,
        String email,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime lastLoginAt,
        String role,
        String status
) {
    public static AdminUserDetailResponse from(User user) {
        return new AdminUserDetailResponse(
                user.getName(),
                user.getLoginId(),
                user.getEmail(),
                user.getPhone(),
                user.getCreatedAt(),
                user.getLastLoginAt(),
                user.getRole().name(),
                user.getStatus().name()
        );
    }
}
