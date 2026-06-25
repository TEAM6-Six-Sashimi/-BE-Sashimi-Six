package com.sashimi.recommendation.application.service;

import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.model.ResumeCertification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OwnedCertificateRecommendationFilter {

    public List<CertificateRecommendation> filter(
            List<CertificateRecommendation> recommendedCertificates,
            Resume resume
    ) {
        if (recommendedCertificates == null || recommendedCertificates.isEmpty()) {
            return List.of();
        }

        if (resume == null || resume.certifications().isEmpty()) {
            return recommendedCertificates;
        }

        Set<String> ownedCertificateNames = resume.certifications().stream()
                .map(ResumeCertification::name)
                .map(this::normalize)
                .collect(Collectors.toSet());

        return recommendedCertificates.stream()
                .filter(certificate ->
                        !ownedCertificateNames.contains(normalize(certificate.name()))
                )
                .toList();
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replaceAll("\\s+", "")
                .toLowerCase();
    }
}