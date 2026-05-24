package com.sashimi.cart.presentation.api.request;

import java.util.List;

public record UpdateCartItemsSelectionRequest(

        List<Long> cartItemIds,
        Boolean selected

) {
}
