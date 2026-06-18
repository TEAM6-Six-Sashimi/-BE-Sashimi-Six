package com.sashimi.credit.presentation.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ConfirmCreditChargeRequest(
        @NotBlank
        String paymentKey,

        @NotBlank
        String orderId,

        @NotNull
        @Positive
        Long amount
) {
}