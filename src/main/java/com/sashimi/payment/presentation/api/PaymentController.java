package com.sashimi.payment.presentation.api;

import com.sashimi.global.exception.ErrorResponse;
import com.sashimi.payment.application.command.CheckoutCartCommand;
import com.sashimi.payment.application.command.PayCourseCommand;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase;
import com.sashimi.payment.application.usecase.PaymentQueryUseCase;
import com.sashimi.payment.presentation.api.response.PaymentHistoryResponse;
import com.sashimi.payment.presentation.api.response.PaymentResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payment", description = "결제 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentCommandUseCase paymentCommandUseCase;
    private final PaymentQueryUseCase paymentQueryUseCase;

    public PaymentController(
            PaymentCommandUseCase paymentCommandUseCase,
            PaymentQueryUseCase paymentQueryUseCase
    ) {
        this.paymentCommandUseCase = paymentCommandUseCase;
        this.paymentQueryUseCase = paymentQueryUseCase;
    }

    @Operation(summary = "장바구니 선택 강의 결제", description = "장바구니에서 선택한 강의들을 크레딧으로 결제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "결제 성공",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "선택한 강의 없음, 구매 불가 강의, 크레딧 부족 또는 잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 수강 중인 강의",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })

    @PostMapping("/cart/checkout")
    public ResponseEntity<PaymentResponse> checkoutCart(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        PaymentCommandUseCase.PaymentResult result =
                paymentCommandUseCase.checkoutCart(new CheckoutCartCommand(principal.getId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentResponse.from(result));
    }


    @Operation(summary = "단일 강의 바로 결제", description = "특정 강의를 장바구니 없이 바로 크레딧으로 결제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "결제 성공",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "구매 불가 강의, 크레딧 부족 또는 잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "강의를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 수강 중인 강의",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/course/{courseId}")
    public ResponseEntity<PaymentResponse> payCourse(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long courseId
    ) {
        PaymentCommandUseCase.PaymentResult result =
                paymentCommandUseCase.payCourse(new PayCourseCommand(principal.getId(), courseId));

        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentResponse.from(result));
    }


    @Operation(summary = "결제 내역 조회", description = "로그인한 사용자의 결제 내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "결제 내역 조회 성공",
                    content = @Content(schema = @Schema(implementation = PaymentHistoryResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/history")
    public ResponseEntity<PaymentHistoryResponse> getPaymentHistory(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                PaymentHistoryResponse.from(paymentQueryUseCase.getPaymentHistory(principal.getId()))
        );
    }
}