package com.sashimi.verification.presentation.api.response;

import com.sashimi.verification.domain.model.VerificationPurpose;

public record EmailVerificationConfirmResult(
        String targetEmail,
        VerificationPurpose purpose,
        boolean verified
) {
}