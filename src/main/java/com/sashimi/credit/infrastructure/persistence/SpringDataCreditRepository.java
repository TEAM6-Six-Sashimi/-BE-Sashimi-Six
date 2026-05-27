package com.sashimi.credit.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataCreditRepository extends JpaRepository<CreditJpaEntity, Long> {

    Optional<CreditJpaEntity> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CreditJpaEntity c where c.userId = :userId")
    Optional<CreditJpaEntity> findByUserIdForUpdate(@Param("userId") Long userId);


}
