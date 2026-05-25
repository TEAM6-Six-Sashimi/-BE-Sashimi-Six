package com.sashimi.auth.dto;

import com.sashimi.verification.domain.model.VerificationPurpose;

public record PasswordResetRequestResponseDto(
        String email,
        VerificationPurpose purpose,
        long expiresInSeconds,
        long resendAvailableInSeconds
) {
}
