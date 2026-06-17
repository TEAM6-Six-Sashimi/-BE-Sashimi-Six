package com.sashimi.cart.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import com.sashimi.cart.domain.model.CartItemType;

public interface SpringDataCartItemRepository extends JpaRepository<CartItemJpaEntity, Long> {

    List<CartItemJpaEntity> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Optional<CartItemJpaEntity> findByIdAndUserId(Long id, Long userId);

    List<CartItemJpaEntity> findAllByUserIdAndSelectedTrueOrderByCreatedAtDesc(Long userId);

    void deleteAllByUserIdAndSelectedTrue(Long userId);

    void deleteAllByUserId(Long userId);

    void deleteByUserIdAndCourseId(Long userId, Long courseId);

    boolean existsByUserIdAndItemTypeAndItemId(Long userId, CartItemType itemType, Long itemId);

    void deleteByUserIdAndItemTypeAndItemId(Long userId, CartItemType itemType, Long itemId);
}