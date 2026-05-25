package com.sashimi.verification.application.result;

import com.sashimi.verification.domain.model.VerificationPurpose;

public record EmailVerificationRequestResult(
        String targetEmail,
        VerificationPurpose purpose,
        long expiresInSeconds,
        long resendAvailableInSeconds
) {
}