package com.sashimi.payment.presentation.api.response;

import com.sashimi.payment.application.usecase.PaymentQueryUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PaymentPreviewResponse(

        @Schema(description = "구매 유형", example = "CART")
        String purchaseType,

        @Schema(description = "결제할 강의 목록")
        List<PaymentPreviewCourseResponse> courses,

        @Schema(description = "총 결제 금액", example = "34800")
        Long totalAmount,

        @Schema(description = "현재 보유 크레딧", example = "50000")
        Long creditBalance,

        @Schema(description = "결제 후 예상 잔액", example = "15200")
        Long balanceAfterPayment,

        @Schema(description = "부족한 크레딧 금액", example = "0")
        Long insufficientAmount,

        @Schema(description = "결제 가능 여부", example = "true")
        boolean payable
) {
    public static PaymentPreviewResponse from(
            PaymentQueryUseCase.PaymentPreview preview
    ) {
        return new PaymentPreviewResponse(
                preview.purchaseType(),
                preview.courses().stream()
                        .map(PaymentPreviewCourseResponse::from)
                        .toList(),
                preview.totalAmount(),
                preview.creditBalance(),
                preview.balanceAfterPayment(),
                preview.insufficientAmount(),
                preview.payable()
        );
    }
}