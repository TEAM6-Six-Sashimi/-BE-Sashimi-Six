package com.sashimi.credit.presentation.api;


import com.sashimi.credit.application.result.CreditBalanceResult;
import com.sashimi.credit.application.usecase.CreditCommandUseCase;
import com.sashimi.credit.application.usecase.CreditQueryUseCase;

import com.sashimi.credit.presentation.api.response.CreditResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.sashimi.credit.application.command.ConfirmCreditChargeCommand;
import com.sashimi.credit.application.command.ReadyCreditChargeCommand;
import com.sashimi.credit.application.result.CreditChargeConfirmResult;
import com.sashimi.credit.application.result.CreditChargeReadyResult;
import com.sashimi.credit.presentation.api.request.ConfirmCreditChargeRequest;
import com.sashimi.credit.presentation.api.request.ReadyCreditChargeRequest;
import com.sashimi.credit.presentation.api.response.CreditChargeConfirmResponse;
import com.sashimi.credit.presentation.api.response.CreditChargeReadyResponse;

@Tag(name = "Credit", description = "크레딧 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/credits")
public class CreditController {

    private final CreditCommandUseCase creditCommandUseCase;
    private final CreditQueryUseCase creditQueryUseCase;

    public CreditController(CreditCommandUseCase creditCommandUseCase, CreditQueryUseCase creditQueryUseCase) {
        this.creditCommandUseCase = creditCommandUseCase;
        this.creditQueryUseCase = creditQueryUseCase;
    }

    @Operation(summary = "내 크레딧 잔액 조회", description = "로그인한 사용자의 현재 크레딧 잔액을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "크레딧 잔액 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/me")
    public ResponseEntity<CreditResponse> getMyCredit(@AuthenticationPrincipal CustomUserPrincipal principal) {
        CreditBalanceResult result = creditQueryUseCase.getBalance(principal.getId());
        return ResponseEntity.ok(CreditResponse.from(result));
    }

    @PostMapping("/toss/ready")
    public ResponseEntity<CreditChargeReadyResponse> readyCreditCharge(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid ReadyCreditChargeRequest request
    ) {
        CreditChargeReadyResult result = creditCommandUseCase.readyCreditCharge(
                new ReadyCreditChargeCommand(principal.getId(), request.amount())
        );

        return ResponseEntity.ok(CreditChargeReadyResponse.from(result));
    }

    @PostMapping("/toss/confirm")
    public ResponseEntity<CreditChargeConfirmResponse> confirmCreditCharge(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid ConfirmCreditChargeRequest request
    ) {
        CreditChargeConfirmResult result = creditCommandUseCase.confirmCreditCharge(
                new ConfirmCreditChargeCommand(
                        principal.getId(),
                        request.paymentKey(),
                        request.orderId(),
                        request.amount()
                )
        );

        return ResponseEntity.ok(CreditChargeConfirmResponse.from(result));
    }


}