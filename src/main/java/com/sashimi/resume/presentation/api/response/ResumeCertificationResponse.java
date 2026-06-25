package com.sashimi.resume.presentation.api.response;

import com.sashimi.resume.domain.model.ResumeCertification;
import com.sashimi.resume.domain.model.ResumeCertificationType;

import java.time.LocalDate;

public record ResumeCertificationResponse(
        String name,
        ResumeCertificationType type,
        String issuer,
        LocalDate acquiredDate,
        String scoreOrGrade
) {

    public static ResumeCertificationResponse from(
            ResumeCertification certification
    ) {
        return new ResumeCertificationResponse(
                certification.name(),
                certification.type(),
                certification.issuer(),
                certification.acquiredDate(),
                certification.scoreOrGrade()
        );
    }
}