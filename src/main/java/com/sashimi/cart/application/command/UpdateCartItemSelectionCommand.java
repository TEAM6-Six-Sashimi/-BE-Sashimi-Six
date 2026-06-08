package com.sashimi.cart.application.command;

public record UpdateCartItemSelectionCommand(

        Long userId,
        Long cartItemId,
        boolean selected

) {
}
