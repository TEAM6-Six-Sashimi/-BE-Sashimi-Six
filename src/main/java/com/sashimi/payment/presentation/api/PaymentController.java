package com.sashimi.payment.presentation.api;

import com.sashimi.global.exception.ErrorResponse;
import com.sashimi.payment.application.command.PaymentCheckoutCommand;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase;
import com.sashimi.payment.application.usecase.PaymentQueryUseCase;
import com.sashimi.payment.presentation.api.request.PaymentCheckoutRequest;
import com.sashimi.payment.presentation.api.response.PaymentHistoryResponse;
import com.sashimi.payment.presentation.api.response.PaymentPreviewResponse;
import com.sashimi.payment.presentation.api.response.PaymentResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payment", description = "강의 결제 API")
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

    @Operation(
            summary = "단일 강의 결제 전 정보 조회",
            description = "강의 상세에서 바로 구매한 강의의 정보, 결제 금액, 보유 크레딧 및 결제 가능 여부를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "결제 전 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = PaymentPreviewResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "구매할 수 없는 강의",
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
    @GetMapping("/course/{courseId}/preview")
    public ResponseEntity<PaymentPreviewResponse> getCoursePreview(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long courseId
    ) {
        PaymentQueryUseCase.PaymentPreview preview =
                paymentQueryUseCase.getCoursePreview(principal.getId(), courseId);

        return ResponseEntity.ok(PaymentPreviewResponse.from(preview));
    }

    @Operation(
            summary = "장바구니 선택 강의 결제 전 정보 조회",
            description = "장바구니에서 선택된 강의 목록, 총 결제 금액, 보유 크레딧 및 결제 가능 여부를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "결제 전 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = PaymentPreviewResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "선택한 강의가 없거나 구매할 수 없는 강의가 포함됨",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 수강 중인 강의가 포함됨",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/cart/preview")
    public ResponseEntity<PaymentPreviewResponse> getCartPreview(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        PaymentQueryUseCase.PaymentPreview preview =
                paymentQueryUseCase.getCartPreview(principal.getId());

        return ResponseEntity.ok(PaymentPreviewResponse.from(preview));
    }

    @Operation(
            summary = "강의 결제",
            description = """
                    단일 강의 또는 장바구니에서 선택한 강의를 크레딧으로 결제합니다.
                    COURSE 결제는 courseId가 필수이고,
                    CART 결제는 courseId를 전달하지 않아야 합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "결제 성공",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "결제 요청 오류, 선택 강의 없음, 결제 동의 누락, 크레딧 부족 또는 구매 불가능한 강의",
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
    @PostMapping("/checkout")
    public ResponseEntity<PaymentResponse> checkout(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid PaymentCheckoutRequest request
    ) {
        PaymentCommandUseCase.PaymentResult result =
                paymentCommandUseCase.checkout(
                        new PaymentCheckoutCommand(
                                principal.getId(),
                                request.purchaseType(),
                                request.courseId(),
                                request.agreed()
                        )
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PaymentResponse.from(result));
    }

    @Operation(
            summary = "사용자 결제 내역 조회",
            description = "로그인한 사용자의 강의 결제 내역을 최신순으로 조회합니다."
    )
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
        PaymentQueryUseCase.PaymentHistory history =
                paymentQueryUseCase.getPaymentHistory(principal.getId());

        return ResponseEntity.ok(PaymentHistoryResponse.from(history));
    }
}