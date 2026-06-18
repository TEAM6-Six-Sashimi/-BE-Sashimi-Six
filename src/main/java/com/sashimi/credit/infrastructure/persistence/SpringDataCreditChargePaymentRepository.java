package com.sashimi.credit.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataCreditChargePaymentRepository extends JpaRepository<CreditChargePaymentJpaEntity, Long> {

    Optional<CreditChargePaymentJpaEntity> findByOrderId(String orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from CreditChargePaymentJpaEntity p where p.orderId = :orderId")
    Optional<CreditChargePaymentJpaEntity> findByOrderIdForUpdate(@Param("orderId") String orderId);
}