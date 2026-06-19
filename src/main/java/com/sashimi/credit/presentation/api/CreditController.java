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
import com.sashimi.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

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


    @Operation(
            summary = "Toss 크레딧 충전 준비",
            description = "충전 금액을 검증하고 Toss 결제에 사용할 주문번호, 주문명 및 결제 금액을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "충전 준비 성공",
                    content = @Content(schema = @Schema(
                            implementation = CreditChargeReadyResponse.class
                    ))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "충전 금액이 최소 금액보다 작거나 1,000 단위가 아님",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            )
    })
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


    @Operation(
            summary = "Toss 크레딧 충전 승인",
            description = "Toss 결제 성공 정보를 검증하고 Toss 승인 API 호출 성공 후 사용자의 크레딧을 충전합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "충전 승인 및 크레딧 충전 성공",
                    content = @Content(schema = @Schema(
                            implementation = CreditChargeConfirmResponse.class
                    ))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "결제 금액 불일치 또는 잘못된 요청",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "다른 사용자의 충전 주문에 접근",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "충전 준비 주문을 찾을 수 없음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 처리된 충전 결제",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "Toss 외부 결제 승인 실패",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            )
    })
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