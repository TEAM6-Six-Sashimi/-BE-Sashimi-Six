package com.sashimi.verification.infrastructure.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataEmailOutboxRepository extends JpaRepository<EmailOutboxJpaEntity, Long> {

    List<EmailOutboxJpaEntity> findFirst100ByStatusOrderByIdAsc(OutboxStatus status);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE EmailOutboxJpaEntity e SET e.status = 'PROCESSING' WHERE e.id = :id AND e.status = 'PENDING'")
    int claimForProcessing(@Param("id") Long id);
}
