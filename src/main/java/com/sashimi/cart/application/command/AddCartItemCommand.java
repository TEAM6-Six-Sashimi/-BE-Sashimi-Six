package com.sashimi.cart.application.command;

public record AddCartItemCommand(
        Long userId,
        Long courseId
) {
}