package com.sashimi.subscription.infrastructure.persistence;

import com.sashimi.subscription.domain.model.SubscriptionStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SpringDataSubscriptionRepository
        extends JpaRepository<SubscriptionJpaEntity, Long> {

    List<SubscriptionJpaEntity>
    findByUserIdAndStatusAndExpiredAtAfterOrderByStartedAtDesc(
            Long userId,
            SubscriptionStatus status,
            LocalDateTime now,
            Pageable pageable
    );
}