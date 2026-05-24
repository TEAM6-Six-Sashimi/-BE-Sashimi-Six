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

@RestController
@RequestMapping("/verifications/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationUseCase emailVerificationUseCase;

    @PostMapping("/request")
    public ResponseEntity<Void> requestEmailVerification(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid EmailVerificationRequest request
    ) {
        Long userId = principal == null ? null : principal.getId();

        emailVerificationUseCase.requestEmailVerification(
                request.toCommand(userId)
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmEmailVerification(
            @RequestBody @Valid ConfirmEmailVerificationRequest request
    ) {
        emailVerificationUseCase.confirmEmailVerification(request.toCommand());

        return ResponseEntity.noContent().build();
    }
}