package com.sashimi.qualification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SpringDataQualificationExamScheduleRepository
        extends JpaRepository<QualificationExamScheduleJpaEntity, Long> {

    Optional<QualificationExamScheduleJpaEntity>
    findByImplYearAndImplSeqAndQualificationTypeCodeAndDescription(
            int implYear,
            int implSeq,
            String qualificationTypeCode,
            String description
    );

    Optional<QualificationExamScheduleJpaEntity>
    findByJmCdAndImplYearAndImplSeqAndDescription(
            String jmCd,
            int implYear,
            int implSeq,
            String description
    );

    Optional<QualificationExamScheduleJpaEntity>
    findFirstByQualificationNameAndDocExamStartDateGreaterThanEqualOrderByDocExamStartDateAsc(
            String qualificationName,
            LocalDate today
    );
}