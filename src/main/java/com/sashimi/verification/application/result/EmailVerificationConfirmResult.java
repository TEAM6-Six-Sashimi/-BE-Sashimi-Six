package com.sashimi.verification.application.result;

import com.sashimi.verification.domain.model.VerificationPurpose;

public record EmailVerificationConfirmResult(
        String targetEmail,
        VerificationPurpose purpose,
        boolean verified
) {
}