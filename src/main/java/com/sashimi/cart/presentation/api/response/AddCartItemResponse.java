package com.sashimi.cart.presentation.api.response;

public record AddCartItemResponse(
        Long courseId,
        Long cartItemId,
        String itemType,
        Long itemId
) {
}