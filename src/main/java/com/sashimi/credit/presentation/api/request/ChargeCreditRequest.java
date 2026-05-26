package com.sashimi.credit.presentation.api.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ChargeCreditRequest(
        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount
) {
}