package com.sashimi.credit.presentation.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ChargeCreditRequest(
        @NotNull
        @Positive
        Long amount
) {
}