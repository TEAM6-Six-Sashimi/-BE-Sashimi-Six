package com.sashimi.credit.presentation.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReadyCreditChargeRequest(
        @NotNull
        @Positive
        Long amount
) {
}