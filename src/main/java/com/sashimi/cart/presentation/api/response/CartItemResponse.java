package com.sashimi.cart.presentation.api.response;

import com.sashimi.cart.application.usecase.CartQueryUseCase;

public record CartItemResponse(
        Long cartItemId,
        String itemType,
        Long itemId,
        Long courseId,
        String title,
        String thumbnail,
        String instructorName,
        Long price,
        boolean selected
) {
    public static CartItemResponse from(CartQueryUseCase.CartItemView view) {
        return new CartItemResponse(
                view.cartItemId(),
                view.itemType(),
                view.itemId(),
                view.courseId(),
                view.title(),
                view.thumbnail(),
                view.instructorName(),
                view.price(),
                view.selected()
        );
    }
}