package com.sashimi.cart.application.usecase;

import java.util.List;

public interface CartQueryUseCase {

    CartView getCart(Long userId);

    CartView getCheckoutCart(Long userId);

    record CartView(
            List<CartItemView> items,
            Long totalPrice,
            int itemCount,
            int selectedItemCount
    ) {
    }

    record CartItemView(
            Long cartItemId,
            Long courseId,
            String title,
            String thumbnail,
            String instructorName,
            Long price,
            boolean selected
    ) {
    }
}