package com.sashimi.payment.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataPaymentIdempotencyRepository
        extends JpaRepository<PaymentIdempotencyJpaEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select p
            from PaymentIdempotencyJpaEntity p
            where p.id = :id
            """)
    Optional<PaymentIdempotencyJpaEntity> findByIdForUpdate(
            @Param("id") Long id
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select p
        from PaymentIdempotencyJpaEntity p
        where p.userId = :userId
          and p.idempotencyKey = :idempotencyKey
        """)
    Optional<PaymentIdempotencyJpaEntity>
    findByUserIdAndIdempotencyKeyForUpdate(
            @Param("userId") Long userId,
            @Param("idempotencyKey") String idempotencyKey
    );
}