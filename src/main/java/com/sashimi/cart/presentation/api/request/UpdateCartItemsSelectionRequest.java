package com.sashimi.cart.presentation.api.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record UpdateCartItemsSelectionRequest(
        @NotEmpty
        List<@NotNull @Positive Long> cartItemIds,

        @NotNull
        Boolean selected
) {
}