package com.sashimi.ncs.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataNcsInfoRepository extends JpaRepository<NcsInfoJpaEntity, Long> {
    Optional<NcsInfoJpaEntity> findByNcsCode(String ncsCode);
}