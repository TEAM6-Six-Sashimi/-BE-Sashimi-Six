package com.sashimi.qualification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataQualificationCodeRepository
        extends JpaRepository<QualificationCodeJpaEntity, Long> {

    Optional<QualificationCodeJpaEntity> findByJmCd(String jmCd);

    List<QualificationCodeJpaEntity> findTop20ByQualificationNameContainingOrderByQualificationNameAsc(
            String keyword
    );

    Optional<QualificationCodeJpaEntity> findFirstByQualificationName(
            String qualificationName
    );

    Optional<QualificationCodeJpaEntity> findFirstByQualificationNameContainingOrderByQualificationNameAsc(
            String keyword
    );
}