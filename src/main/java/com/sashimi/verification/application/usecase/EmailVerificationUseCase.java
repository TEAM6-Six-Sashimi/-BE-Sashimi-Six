package com.sashimi.verification.application.usecase;

import com.sashimi.verification.application.command.ConfirmEmailVerificationCommand;
import com.sashimi.verification.application.command.RequestEmailVerificationCommand;
import com.sashimi.verification.domain.model.VerificationPurpose;

public interface EmailVerificationUseCase {

    void requestEmailVerification(RequestEmailVerificationCommand command);

    void confirmEmailVerification(ConfirmEmailVerificationCommand command);

    void validateVerifiedEmail(String targetEmail, VerificationPurpose purpose);
}