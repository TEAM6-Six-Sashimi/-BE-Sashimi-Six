package com.sashimi.auth.application.policy;

import com.sashimi.user.domain.model.User;

public record SignupEligibility(
        User referrer
) {
    public boolean hasReferrer() {
        return referrer != null;
    }
}
