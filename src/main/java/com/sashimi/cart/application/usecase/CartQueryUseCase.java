package com.sashimi.cart.application.usecase;

import java.math.BigDecimal;
import java.util.List;

public interface CartQueryUseCase {

    CartView getCart(Long userId);
    CartView getCheckoutCart(Long userId);

    record CartView(
            List<CartItemView> items,
            BigDecimal totalPrice,
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
            BigDecimal price,
            boolean selected
    ) {
    }
}