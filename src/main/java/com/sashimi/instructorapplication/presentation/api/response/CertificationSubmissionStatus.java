package com.sashimi.instructorapplication.presentation.api.response;

import com.sashimi.instructorapplication.domain.model.VerificationStatus;

public enum CertificationSubmissionStatus {
    PENDING,
    SUBMITTED;

    public static CertificationSubmissionStatus from(VerificationStatus verificationStatus) {
        return verificationStatus == VerificationStatus.PENDING ? PENDING : SUBMITTED;
    }
}
