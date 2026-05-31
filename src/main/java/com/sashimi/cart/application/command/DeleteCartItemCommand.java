package com.sashimi.cart.application.command;

import java.util.List;

public record DeleteCartItemCommand(
        Long userId,
        List<Long> cartItemIds
) {
}