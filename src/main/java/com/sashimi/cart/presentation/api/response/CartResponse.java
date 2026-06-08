package com.sashimi.cart.presentation.api.response;

import com.sashimi.cart.application.usecase.CartQueryUseCase;

import java.util.List;

public record CartResponse(
        List<CartItemResponse> items,
        Long totalPrice,
        int itemCount,
        int selectedItemCount
) {
    public static CartResponse from(CartQueryUseCase.CartView view) {
        return new CartResponse(
                view.items().stream()
                        .map(CartItemResponse::from)
                        .toList(),
                view.totalPrice(),
                view.itemCount(),
                view.selectedItemCount()
        );
    }
}