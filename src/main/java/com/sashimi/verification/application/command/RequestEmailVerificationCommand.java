package com.sashimi.verification.application.command;

import com.sashimi.verification.domain.model.VerificationPurpose;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RequestEmailVerificationCommand {

    private final String targetEmail;
    private final VerificationPurpose purpose;
    private final Long userId;
}