package com.sashimi.resume.presentation.api.request;

import com.sashimi.resume.domain.model.ResumeCertification;
import com.sashimi.resume.domain.model.ResumeCertificationType;

import java.time.LocalDate;

public record ResumeCertificationRequest(
        String name,
        ResumeCertificationType type,
        String issuer,
        LocalDate acquiredDate,
        String scoreOrGrade
) {

    public ResumeCertification toDomain() {
        return new ResumeCertification(
                name,
                type,
                issuer,
                acquiredDate,
                scoreOrGrade
        );
    }
}