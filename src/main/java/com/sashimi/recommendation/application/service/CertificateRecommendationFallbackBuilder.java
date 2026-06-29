package com.sashimi.recommendation.application.service;

import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import com.sashimi.recommendation.domain.model.JobFitAnalysis;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class CertificateRecommendationFallbackBuilder {

    public List<CertificateRecommendation> build(
            List<CertificateRecommendation> certificates,
            JobFitAnalysis fitAnalysis
    ) {
        if (certificates != null && !certificates.isEmpty()) {
            return certificates;
        }

        if (fitAnalysis == null || fitAnalysis.certification() == null) {
            return List.of();
        }

        var missingItems = fitAnalysis.certification().missingItems();
        if (missingItems == null || missingItems.isEmpty()) {
            return List.of();
        }

        return missingItems.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .distinct()
                .map(this::toCertificateRecommendation)
                .toList();
    }

    private CertificateRecommendation toCertificateRecommendation(String name) {
        return new CertificateRecommendation(
                null,
                name,
                "채용공고의 자격증 조건과 비교했을 때 보완이 필요한 자격증입니다.",
                List.of(name),
                "확인 필요",
                null,
                null,
                null
        );
    }
}
