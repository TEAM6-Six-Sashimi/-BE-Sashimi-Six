package com.sashimi.cart.presentation.api.request;

import jakarta.validation.constraints.NotNull;

public record UpdateCartItemSelectionRequest(
        @NotNull
        Boolean selected
) {
}