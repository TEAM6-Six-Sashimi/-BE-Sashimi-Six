package com.sashimi.cart.presentation.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddCartItemRequest(
        @NotNull
        @Positive
        Long courseId
) {
}