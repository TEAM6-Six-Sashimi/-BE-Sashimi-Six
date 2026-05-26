package com.sashimi.credit.presentation.api;

import com.sashimi.credit.application.command.ChargeCreditCommand;
import com.sashimi.credit.application.result.CreditBalanceResult;
import com.sashimi.credit.application.usecase.CreditCommandUseCase;
import com.sashimi.credit.application.usecase.CreditQueryUseCase;
import com.sashimi.credit.presentation.api.request.ChargeCreditRequest;
import com.sashimi.credit.presentation.api.response.CreditResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/credits")
public class CreditController {

    private final CreditCommandUseCase creditCommandUseCase;
    private final CreditQueryUseCase creditQueryUseCase;

    public CreditController(
            CreditCommandUseCase creditCommandUseCase,
            CreditQueryUseCase creditQueryUseCase
    ) {
        this.creditCommandUseCase = creditCommandUseCase;
        this.creditQueryUseCase = creditQueryUseCase;
    }

    @GetMapping("/me")
    public ResponseEntity<CreditResponse> getMyCredit(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        CreditBalanceResult result = creditQueryUseCase.getBalance(principal.getId());
        return ResponseEntity.ok(CreditResponse.from(result));
    }

    @PostMapping("/charge")
    public ResponseEntity<CreditResponse> chargeCredit(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid ChargeCreditRequest request
    ) {
        CreditBalanceResult result = creditCommandUseCase.chargeCredit(
                new ChargeCreditCommand(principal.getId(), request.amount())
        );

        return ResponseEntity.ok(CreditResponse.from(result));
    }
}