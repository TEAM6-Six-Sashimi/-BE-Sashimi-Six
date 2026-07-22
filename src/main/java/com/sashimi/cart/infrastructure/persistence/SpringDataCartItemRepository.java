package com.sashimi.cart.infrastructure.persistence;

import  org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
        select c.id
        from CartItemJpaEntity c
        where c.userId = :userId
          and c.id in :cartItemIds
        """)
    List<Long> findOwnedIds(
            @Param("userId") Long userId,
            @Param("cartItemIds") List<Long> cartItemIds
    );

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query("""
        update CartItemJpaEntity c
        set c.selected = :selected
        where c.userId = :userId
          and c.id in :cartItemIds
        """)
    int updateSelectedByIds(
            @Param("userId") Long userId,
            @Param("cartItemIds") List<Long> cartItemIds,
            @Param("selected") boolean selected
    );

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query("""
        delete from CartItemJpaEntity c
        where c.userId = :userId
          and c.id in :cartItemIds
        """)
    int deleteAllByIds(
            @Param("userId") Long userId,
            @Param("cartItemIds") List<Long> cartItemIds
    );
}