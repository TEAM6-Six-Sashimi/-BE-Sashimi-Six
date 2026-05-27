package com.sashimi.credit.presentation.api;

import com.sashimi.credit.application.command.ChargeCreditCommand;
import com.sashimi.credit.application.result.CreditBalanceResult;
import com.sashimi.credit.application.usecase.CreditCommandUseCase;
import com.sashimi.credit.application.usecase.CreditQueryUseCase;
import com.sashimi.credit.presentation.api.request.ChargeCreditRequest;
import com.sashimi.credit.presentation.api.response.CreditResponse;
import com.sashimi.global.exception.ErrorResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Credit", description = "크레딧 API")
@SecurityRequirement(name = "bearerAuth")
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

    @Operation(summary = "내 크레딧 잔액 조회", description = "로그인한 사용자의 현재 크레딧 잔액을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "크레딧 잔액 조회 성공",
                    content = @Content(schema = @Schema(implementation = CreditResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/me")
    public ResponseEntity<CreditResponse> getMyCredit(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        CreditBalanceResult result = creditQueryUseCase.getBalance(principal.getId());
        return ResponseEntity.ok(CreditResponse.from(result));
    }

    @Operation(summary = "크레딧 충전", description = "로그인한 사용자의 크레딧을 충전합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "크레딧 충전 성공",
                    content = @Content(schema = @Schema(implementation = CreditResponse.class))),
            @ApiResponse(responseCode = "400", description = "충전 금액이 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/charge")
    public ResponseEntity<CreditResponse> chargeCredit(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid ChargeCreditRequest request
    ) {
        CreditBalanceResult result = creditCommandUseCase.chargeCredit(
                new ChargeCreditCommand(principal.getId(), request.amount())
        );

        return ResponseEntity.ok(CreditResponse.from(result));
    }
}