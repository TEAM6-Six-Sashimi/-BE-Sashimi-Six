package com.sashimi.recommendation.application.service;

import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import com.sashimi.resume.domain.model.Resume;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OwnedCertificateRecommendationFilter {

    public List<CertificateRecommendation> filter(
            List<CertificateRecommendation> certificates,
            Resume resume
    ) {
        if (certificates == null || certificates.isEmpty()) {
            return List.of();
        }

        if (resume == null || resume.certifications() == null || resume.certifications().isEmpty()) {
            return certificates;
        }

        Set<String> ownedCertificateNames = resume.certifications().stream()
                .map(certification -> normalize(certification.name()))
                .collect(Collectors.toSet());

        return certificates.stream()
                .filter(certificate -> !ownedCertificateNames.contains(normalize(certificate.name())))
                .toList();
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.replaceAll("\\s+", "")
                .toLowerCase();
    }
}