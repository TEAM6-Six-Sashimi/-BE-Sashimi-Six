package com.sashimi.credit.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataCreditRepository extends JpaRepository<CreditJpaEntity, Long> {

    Optional<CreditJpaEntity> findByUserId(Long userId);
}