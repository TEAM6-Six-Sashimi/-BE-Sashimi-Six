package com.sashimi.cart.presentation.api.response;

import com.sashimi.cart.application.usecase.CartQueryUseCase;

import java.math.BigDecimal;

public record CartItemResponse(
        Long cartItemId,
        Long courseId,
        String title,
        String thumbnail,
        String instructorName,
        BigDecimal price,
        boolean selected
) {
    public static CartItemResponse from(CartQueryUseCase.CartItemView view) {
        return new CartItemResponse(
                view.cartItemId(),
                view.courseId(),
                view.title(),
                view.thumbnail(),
                view.instructorName(),
                view.price(),
                view.selected()
        );
    }
}