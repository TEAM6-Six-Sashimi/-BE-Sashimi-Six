package com.sashimi.cart.application.command;

public record DeleteCartItemCommand(
        Long userId,
        Long cartItemId
) {
}