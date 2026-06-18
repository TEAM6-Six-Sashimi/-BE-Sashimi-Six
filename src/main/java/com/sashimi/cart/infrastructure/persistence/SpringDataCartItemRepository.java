package com.sashimi.cart.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataCartItemRepository extends JpaRepository<CartItemJpaEntity, Long> {

    List<CartItemJpaEntity> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Optional<CartItemJpaEntity> findByIdAndUserId(Long id, Long userId);

    List<CartItemJpaEntity> findAllByUserIdAndSelectedTrueOrderByCreatedAtDesc(Long userId);

    void deleteAllByUserIdAndSelectedTrue(Long userId);

    void deleteAllByUserId(Long userId);

    void deleteByUserIdAndCourseId(Long userId, Long courseId);

}