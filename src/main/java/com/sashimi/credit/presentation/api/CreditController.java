package com.sashimi.credit.presentation.api;

import com.sashimi.credit.application.service.CreditService;
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

    private final CreditService creditService;

    public CreditController(CreditService creditService) {
        this.creditService = creditService;
    }

    @GetMapping("/me")
    public ResponseEntity<CreditResponse> getMyCredit(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                new CreditResponse(creditService.getBalance(principal.getId()))
        );
    }

    @PostMapping("/charge")
    public ResponseEntity<CreditResponse> chargeCredit(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid ChargeCreditRequest request
    ) {
        return ResponseEntity.ok(
                new CreditResponse(
                        creditService.chargeCredit(principal.getId(), request.amount())
                )
        );
    }
}