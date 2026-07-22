package com.sashimi.cart.domain.repository;

import com.sashimi.cart.domain.model.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository {

    CartItem save(CartItem cartItem);

    List<CartItem> findAllByUserId(Long userId);

    List<CartItem> findAllSelectedByUserId(Long userId);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Optional<CartItem> findByIdAndUserId(Long cartItemId, Long userId);

    List<Long> findOwnedIds(Long userId, List<Long> cartItemIds);

    void updateSelectedByIds(Long userId, List<Long> cartItemIds, boolean selected);

    void deleteAllByIds(Long userId, List<Long> cartItemIds);

    void delete(CartItem cartItem);

    void deleteAllSelectedByUserId(Long userId);

    void deleteAllByUserId(Long userId);

    void deleteByUserIdAndCourseId(Long userId, Long courseId);


}