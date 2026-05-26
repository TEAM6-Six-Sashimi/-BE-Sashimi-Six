package com.sashimi.verification.application.usecase;

import com.sashimi.verification.application.command.ConfirmEmailVerificationCommand;
import com.sashimi.verification.application.command.RequestEmailVerificationCommand;
import com.sashimi.verification.presentation.api.response.EmailVerificationConfirmResult;
import com.sashimi.verification.presentation.api.response.EmailVerificationRequestResult;
import com.sashimi.verification.domain.model.VerificationPurpose;

public interface EmailVerificationUseCase {

    EmailVerificationRequestResult requestEmailVerification(RequestEmailVerificationCommand command);

    EmailVerificationConfirmResult confirmEmailVerification(ConfirmEmailVerificationCommand command);

    void validateVerifiedEmail(String targetEmail, VerificationPurpose purpose);
}