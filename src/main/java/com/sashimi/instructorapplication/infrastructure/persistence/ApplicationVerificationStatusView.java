package com.sashimi.instructorapplication.infrastructure.persistence;

import com.sashimi.instructorapplication.domain.model.VerificationStatus;

public interface ApplicationVerificationStatusView {
    Long getApplicationId();
    VerificationStatus getStatus();
}
