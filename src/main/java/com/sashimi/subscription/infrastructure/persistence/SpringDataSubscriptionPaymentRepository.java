package com.sashimi.subscription.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSubscriptionPaymentRepository
        extends JpaRepository<SubscriptionPaymentJpaEntity, Long> {

    Page<SubscriptionPaymentJpaEntity>
    findAllByUserIdOrderByPaidAtDescIdDesc(
            Long userId,
            Pageable pageable
    );
}