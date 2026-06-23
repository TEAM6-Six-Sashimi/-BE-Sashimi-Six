package com.sashimi.subscription.presentation.api;

import com.sashimi.global.exception.ErrorResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import com.sashimi.subscription.application.usecase.SubscriptionCommandUseCase;
import com.sashimi.subscription.application.usecase.SubscriptionQueryUseCase;
import com.sashimi.subscription.presentation.api.response.MySubscriptionResponse;
import com.sashimi.subscription.presentation.api.response.SubscriptionPaymentHistoryResponse;
import com.sashimi.subscription.presentation.api.response.SubscriptionPlansResponse;
import com.sashimi.subscription.presentation.api.response.SubscriptionPreviewResponse;
import com.sashimi.subscription.presentation.api.response.CancelSubscriptionResponse;
import org.springframework.web.bind.annotation.PostMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Tag(
        name = "Subscription",
        description = "AI 구독권 API"
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionQueryUseCase subscriptionQueryUseCase;
    private final SubscriptionCommandUseCase subscriptionCommandUseCase;

    public SubscriptionController(
            SubscriptionQueryUseCase subscriptionQueryUseCase,
            SubscriptionCommandUseCase subscriptionCommandUseCase
    ) {
        this.subscriptionQueryUseCase = subscriptionQueryUseCase;
        this.subscriptionCommandUseCase = subscriptionCommandUseCase;
    }

    @Operation(
            summary = "AI 구독권 플랜 목록 조회",
            description = "구매 가능한 월간 및 연간 플랜을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "플랜 목록 조회 성공",
                    content = @Content(
                            schema = @Schema(
                                    implementation =
                                            SubscriptionPlansResponse.class
                            )
                    )
            )
    })
    @GetMapping("/plans")
    public ResponseEntity<SubscriptionPlansResponse> getPlans() {
        return ResponseEntity.ok(SubscriptionPlansResponse.from(subscriptionQueryUseCase.getPlans()));
    }

    @Operation(
            summary = "AI 구독권 결제 전 정보 조회",
            description = """
                    선택한 구독 플랜과 현재 크레딧 잔액,
                    결제 후 예상 잔액 및 구매 가능 여부를 조회합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "결제 전 정보 조회 성공",
                    content = @Content(
                            schema = @Schema(
                                    implementation =
                                            SubscriptionPreviewResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 플랜",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/plans/{planCode}/preview")
    public ResponseEntity<SubscriptionPreviewResponse> getPreview(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable String planCode) {
        return ResponseEntity.ok(
                SubscriptionPreviewResponse.from(
                        subscriptionQueryUseCase.getPreview(principal.getId(), planCode)));
    }

    @Operation(
            summary = "내 현재 AI 구독 상태 조회",
            description = "현재 활성화된 AI 구독 상태를 조회합니다."
    )
    @GetMapping("/me")
    public ResponseEntity<MySubscriptionResponse> getMySubscription(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        return ResponseEntity.ok(
                MySubscriptionResponse.from(
                        subscriptionQueryUseCase
                                .getMySubscription(
                                        principal.getId()
                                )
                )
        );
    }

    @Operation(
            summary = "내 AI 구독 결제 내역 조회",
            description = "최초 결제 및 갱신 결제 내역을 조회합니다.")

    @GetMapping("/payments")
    public ResponseEntity<SubscriptionPaymentHistoryResponse> getPaymentHistory(
            @AuthenticationPrincipal CustomUserPrincipal principal,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
            @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
            int size
    ) {
        return ResponseEntity.ok(
                SubscriptionPaymentHistoryResponse.from(
                        subscriptionQueryUseCase
                                .getPaymentHistory(
                                        principal.getId(),
                                        page,
                                        size
                                )
                )
        );
    }

    @Operation(
            summary = "AI 구독 해지",
            description = """
                다음 자동 갱신을 중단합니다.
                해지 후에도 현재 구독 만료일까지 AI 기능을 이용할 수 있습니다.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "구독 해지 성공",
                    content = @Content(
                            schema = @Schema(
                                    implementation =
                                            CancelSubscriptionResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "활성 구독 없음",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 해지 신청됨",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @PostMapping("/me/cancel")
    public ResponseEntity<CancelSubscriptionResponse> cancelSubscription(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                CancelSubscriptionResponse.from(
                        subscriptionCommandUseCase.cancel(principal.getId()))
        );
    }
}