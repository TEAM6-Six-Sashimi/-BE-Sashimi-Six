package com.sashimi.verification.application.command;

import com.sashimi.verification.domain.model.VerificationPurpose;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ConfirmEmailVerificationCommand {

    private final String targetEmail;
    private final VerificationPurpose purpose;
    private final String code;
}
