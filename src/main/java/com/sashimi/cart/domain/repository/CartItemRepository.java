package com.sashimi.cart.domain.repository;

import com.sashimi.cart.domain.model.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository {

    CartItem save(CartItem cartItem);

    List<CartItem> findAllByUserId(Long userId);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Optional<CartItem> findByIdAndUserId(Long cartItemId, Long userId);

    void delete(CartItem cartItem);
}