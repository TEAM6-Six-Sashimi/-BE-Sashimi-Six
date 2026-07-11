package com.sashimi.coffeechat.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCoffeeChatMessageRepository extends JpaRepository<CoffeeChatMessageJpaEntity, Long> {

    Page<CoffeeChatMessageJpaEntity> findAllByCoffeeChatIdOrderByCreatedAtAsc(Long coffeeChatId, Pageable pageable);
}
