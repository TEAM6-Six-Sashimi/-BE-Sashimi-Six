package com.sashimi.cart.domain.repository;

import com.sashimi.cart.domain.model.CartItem;

import java.util.List;
import java.util.Optional;
import com.sashimi.cart.domain.model.CartItemType;

public interface CartItemRepository {

    CartItem save(CartItem cartItem);

    List<CartItem> findAllByUserId(Long userId);

    List<CartItem> findAllSelectedByUserId(Long userId);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Optional<CartItem> findByIdAndUserId(Long cartItemId, Long userId);

    void delete(CartItem cartItem);

    void deleteAllSelectedByUserId(Long userId);

    void deleteAllByUserId(Long userId);

    void deleteByUserIdAndCourseId(Long userId, Long courseId);

    boolean existsByUserIdAndItemTypeAndItemId(Long userId, CartItemType itemType, Long itemId);

    void deleteByUserIdAndItemTypeAndItemId(Long userId, CartItemType itemType, Long itemId);
}