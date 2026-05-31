package com.sashimi.cart.presentation.api.request;

import java.util.List;

public record DeleteCartItemsRequest(
        List<Long> cartItemIds
) {
}