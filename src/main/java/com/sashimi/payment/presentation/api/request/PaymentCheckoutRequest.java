package com.sashimi.payment.presentation.api.request;

import com.sashimi.payment.application.command.PaymentPurchaseType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentCheckoutRequest(

        @Schema(
                description = "구매 유형",
                example = "COURSE",
                allowableValues = {"COURSE", "CART"}
        )
        @NotNull(message = "구매 유형은 필수입니다.")
        PaymentPurchaseType purchaseType,

        @Schema(
                description = "단일 구매할 강의 ID. COURSE일 때 필수이며 CART일 때는 전달하지 않습니다.",
                example = "10",
                nullable = true
        )
        @Positive(message = "강의 ID는 양수여야 합니다.")
        Long courseId,

        @Schema(
                description = "결제 약관 동의 여부. 반드시 true여야 합니다.",
                example = "true"
        )
        @NotNull(message = "결제 동의 여부는 필수입니다.")
        @AssertTrue(message = "결제 진행을 위해 결제 동의가 필요합니다.")
        Boolean agreed
) {
}