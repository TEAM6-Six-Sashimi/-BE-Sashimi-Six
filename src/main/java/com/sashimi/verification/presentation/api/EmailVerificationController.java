package com.sashimi.verification.presentation.api;

import com.sashimi.security.principal.CustomUserPrincipal;
import com.sashimi.verification.application.usecase.EmailVerificationUseCase;
import com.sashimi.verification.presentation.api.request.ConfirmEmailVerificationRequest;
import com.sashimi.verification.presentation.api.request.EmailVerificationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.sashimi.verification.presentation.api.response.EmailVerificationConfirmResult;
import com.sashimi.verification.presentation.api.response.EmailVerificationRequestResult;
import com.sashimi.verification.presentation.api.response.EmailVerificationConfirmResponse;
import com.sashimi.verification.presentation.api.response.EmailVerificationRequestResponse;

@RestController
@RequestMapping("/verifications/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationUseCase emailVerificationUseCase;

    @PostMapping("/request")
    public ResponseEntity<EmailVerificationRequestResponse> requestEmailVerification(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid EmailVerificationRequest request
    ) {
        Long userId = principal == null ? null : principal.getId();

        EmailVerificationRequestResult result = emailVerificationUseCase.requestEmailVerification(
                request.toCommand(userId)
        );

        return ResponseEntity.ok(new EmailVerificationRequestResponse(
                result.targetEmail(),
                result.purpose(),
                result.expiresInSeconds(),
                result.resendAvailableInSeconds()
        ));
    }

    @PostMapping("/confirm")
    public ResponseEntity<EmailVerificationConfirmResponse> confirmEmailVerification(
            @RequestBody @Valid ConfirmEmailVerificationRequest request
    ) {
        EmailVerificationConfirmResult result =
                emailVerificationUseCase.confirmEmailVerification(request.toCommand());

        return ResponseEntity.ok(new EmailVerificationConfirmResponse(
                result.targetEmail(),
                result.purpose(),
                result.verified()
        ));
    }
}