package com.sashimi.resume.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

import java.time.LocalDate;

public class ResumeCertification {

    private final String name;
    private final ResumeCertificationType type;
    private final String issuer;
    private final LocalDate acquiredDate;
    private final String scoreOrGrade;

    public ResumeCertification(
            String name,
            ResumeCertificationType type,
            String issuer,
            LocalDate acquiredDate,
            String scoreOrGrade
    ) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (type == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (issuer == null || issuer.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (acquiredDate == null || acquiredDate.isAfter(LocalDate.now())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String normalizedScoreOrGrade = normalize(scoreOrGrade);

        if (normalizedScoreOrGrade != null && normalizedScoreOrGrade.length() > 100) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        this.name = name.trim();
        this.type = type;
        this.issuer = issuer.trim();
        this.acquiredDate = acquiredDate;
        this.scoreOrGrade = normalizedScoreOrGrade;

    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public String name() {
        return name;
    }

    public ResumeCertificationType type() {
        return type;
    }

    public String issuer() {
        return issuer;
    }

    public LocalDate acquiredDate() {
        return acquiredDate;
    }

    public String scoreOrGrade() {
        return scoreOrGrade;
    }
}