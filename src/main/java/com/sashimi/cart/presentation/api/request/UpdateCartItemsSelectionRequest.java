package com.sashimi.cart.presentation.api.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateCartItemsSelectionRequest(
        @NotEmpty
        @Size(max = 100)
        List<@NotNull @Positive Long> cartItemIds,

        @NotNull
        Boolean selected
) {
}