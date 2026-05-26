package com.sashimi.auth.dto;

public record PasswordResetConfirmResponseDto(
        boolean passwordReset,
        boolean requiresLogin
) {
}
