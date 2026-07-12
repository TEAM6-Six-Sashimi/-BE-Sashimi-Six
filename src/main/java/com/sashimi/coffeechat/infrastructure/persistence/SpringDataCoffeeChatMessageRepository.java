package com.sashimi.coffeechat.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataCoffeeChatMessageRepository extends JpaRepository<CoffeeChatMessageJpaEntity, Long> {

    Page<CoffeeChatMessageJpaEntity> findAllByCoffeeChatIdOrderByCreatedAtAsc(Long coffeeChatId, Pageable pageable);

    @Query("SELECT DISTINCT m.coffeeChatId FROM CoffeeChatMessageJpaEntity m "
            + "WHERE m.coffeeChatId IN :coffeeChatIds AND m.isRead = false AND m.senderId <> :excludeSenderId")
    List<Long> findCoffeeChatIdsWithUnreadMessages(
            @Param("coffeeChatIds") List<Long> coffeeChatIds, @Param("excludeSenderId") Long excludeSenderId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE CoffeeChatMessageJpaEntity m SET m.isRead = true "
            + "WHERE m.coffeeChatId = :coffeeChatId AND m.senderId <> :readerId AND m.isRead = false")
    void markAllAsRead(@Param("coffeeChatId") Long coffeeChatId, @Param("readerId") Long readerId);

    @Query("SELECT m FROM CoffeeChatMessageJpaEntity m WHERE m.coffeeChatId IN :coffeeChatIds "
            + "AND m.createdAt = (SELECT MAX(m2.createdAt) FROM CoffeeChatMessageJpaEntity m2 "
            + "WHERE m2.coffeeChatId = m.coffeeChatId)")
    List<CoffeeChatMessageJpaEntity> findLatestMessagesByCoffeeChatIds(
            @Param("coffeeChatIds") List<Long> coffeeChatIds);
}
