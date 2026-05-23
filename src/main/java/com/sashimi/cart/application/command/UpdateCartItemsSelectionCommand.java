package com.sashimi.cart.application.command;

import java.util.List;

public record UpdateCartItemsSelectionCommand(

        Long userId,
        List<Long> cartItemIds,
        boolean selected

) {
}
