package com.sashimi.credit.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface SpringDataCreditRepository extends JpaRepository<CreditJpaEntity, Long> {

    Optional<CreditJpaEntity> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<CreditJpaEntity> findByUserIdForUpdate(Long userId);


}