package com.sashimi.verification.infrastructure.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataEmailOutboxRepository extends JpaRepository<EmailOutboxJpaEntity, Long> {

    List<EmailOutboxJpaEntity> findByStatus(OutboxStatus status);
}
