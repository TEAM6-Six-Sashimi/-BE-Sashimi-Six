package com.sashimi.payment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataPaymentRepository extends JpaRepository<PaymentJpaEntity, Long> {

    List<PaymentJpaEntity> findAllByUserIdOrderByCreatedAtDescIdDesc(Long userId);
}