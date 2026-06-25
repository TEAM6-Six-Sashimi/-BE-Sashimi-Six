package com.sashimi.qualification.presentation.api.response;

import com.sashimi.qualification.infrastructure.persistence.QualificationExamScheduleJpaEntity;

import java.time.LocalDate;

public record QualificationExamScheduleResponse(
        String qualificationName,
        String jmCd,
        int implYear,
        int implSeq,
        String qualificationTypeName,
        String description,
        LocalDate applicationStartDate,
        LocalDate applicationEndDate,
        LocalDate examStartDate,
        LocalDate examEndDate
) {

    public static QualificationExamScheduleResponse from(
            QualificationExamScheduleJpaEntity entity
    ) {
        return new QualificationExamScheduleResponse(
                entity.getQualificationName(),
                entity.getJmCd(),
                entity.getImplYear(),
                entity.getImplSeq(),
                entity.getQualificationTypeName(),
                entity.getDescription(),
                entity.getDocRegStartDate(),
                entity.getDocRegEndDate(),
                entity.getDocExamStartDate(),
                entity.getDocExamEndDate()
        );
    }
}