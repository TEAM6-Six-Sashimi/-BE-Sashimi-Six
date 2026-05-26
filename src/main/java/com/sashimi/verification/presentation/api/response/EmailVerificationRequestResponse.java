package com.sashimi.verification.presentation.api.response;

import com.sashimi.verification.domain.model.VerificationPurpose;

public record EmailVerificationRequestResponse(
        String targetEmail,
        VerificationPurpose purpose,
        long expiresInSeconds,
        long resendAvailableInSeconds
) {
}