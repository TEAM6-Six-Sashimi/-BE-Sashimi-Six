package com.sashimi.subscription.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataSubscriptionRepository
        extends JpaRepository<SubscriptionJpaEntity, Long> {

    @Query("""
        select s
        from SubscriptionJpaEntity s
        where s.userId = :userId
          and (
                (
                    s.status = com.sashimi.subscription.domain.model.SubscriptionStatus.ACTIVE
                    and s.expiredAt > :now
                )
                or
                (
                    s.status = com.sashimi.subscription.domain.model.SubscriptionStatus.PAST_DUE
                    and s.gracePeriodUntil is not null
                    and s.gracePeriodUntil > :now
                )
          )
        order by s.startedAt desc
        """)
    List<SubscriptionJpaEntity> findUsableByUserId(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    select s
    from SubscriptionJpaEntity s
    where s.userId = :userId
      and (
            (
                s.status = com.sashimi.subscription.domain.model.SubscriptionStatus.ACTIVE
                and s.expiredAt > :now
            )
            or
            (
                s.status = com.sashimi.subscription.domain.model.SubscriptionStatus.PAST_DUE
                and s.gracePeriodUntil is not null
                and s.gracePeriodUntil > :now
            )
      )
    """)
    Optional<SubscriptionJpaEntity> findActiveByUserIdForUpdate(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select s
            from SubscriptionJpaEntity s
            where s.id = :subscriptionId
            """)
    Optional<SubscriptionJpaEntity> findByIdForUpdate(
            @Param("subscriptionId") Long subscriptionId
    );

    @Query("""
        select s.id
        from SubscriptionJpaEntity s
        where s.status in (
            com.sashimi.subscription.domain.model.SubscriptionStatus.ACTIVE,
            com.sashimi.subscription.domain.model.SubscriptionStatus.PAST_DUE
        )
          and s.autoRenew = true
          and s.nextBillingAt is not null
          and s.nextBillingAt <= :now
        """)
    List<Long> findRenewalDueIds(
            @Param("now") LocalDateTime now
    );

    @Query("""
            select s.id
            from SubscriptionJpaEntity s
            where s.status = com.sashimi.subscription.domain.model.SubscriptionStatus.ACTIVE
              and s.autoRenew = false
              and s.expiredAt is not null
              and s.expiredAt <= :now
            """)
    List<Long> findExpirationDueIds(
            @Param("now") LocalDateTime now
    );
}