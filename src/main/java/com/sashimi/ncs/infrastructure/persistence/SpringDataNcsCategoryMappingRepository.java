package com.sashimi.ncs.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataNcsCategoryMappingRepository
        extends JpaRepository<NcsCategoryMappingJpaEntity, Long> {

    List<NcsCategoryMappingJpaEntity> findByActiveTrue();

    List<NcsCategoryMappingJpaEntity> findByDutyCdAndActiveTrue(String dutyCd);
}