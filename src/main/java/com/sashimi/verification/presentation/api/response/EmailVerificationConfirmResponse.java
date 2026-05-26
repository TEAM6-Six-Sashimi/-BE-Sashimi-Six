package com.sashimi.verification.presentation.api.response;

import com.sashimi.verification.domain.model.VerificationPurpose;

public record EmailVerificationConfirmResponse(
        String targetEmail,
        VerificationPurpose purpose,
        boolean verified
) {
}
