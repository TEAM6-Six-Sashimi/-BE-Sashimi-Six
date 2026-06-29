package com.sashimi.recommendation.presentation.api.response;

import com.sashimi.recommendation.domain.model.CertificateRecommendation;

import java.time.LocalDate;
import java.util.List;

public record CertificateRecommendationResponse(
        Long certificationId,
        String name,
        String reason,
        List<String> relatedSkills,
        String difficulty,
        LocalDate nextExamDate,
        LocalDate applicationStartDate,
        LocalDate applicationEndDate,
        String scheduleStatus
) {
    public static CertificateRecommendationResponse from(CertificateRecommendation certificate) {
        return new CertificateRecommendationResponse(
                certificate.certificationId(),
                certificate.name(),
                certificate.reason(),
                certificate.relatedSkills(),
                certificate.difficulty(),
                certificate.nextExamDate(),
                certificate.applicationStartDate(),
                certificate.applicationEndDate(),
                resolveScheduleStatus(certificate)
        );
    }

    private static String resolveScheduleStatus(CertificateRecommendation certificate) {
        if (certificate.nextExamDate() == null) {
            return "NOT_AVAILABLE";
        }

        return "AVAILABLE";
    }
}