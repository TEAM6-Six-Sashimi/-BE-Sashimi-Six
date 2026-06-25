package com.sashimi.recommendation.application.service;

import com.sashimi.qualification.infrastructure.persistence.QualificationExamScheduleJpaEntity;
import com.sashimi.qualification.infrastructure.persistence.SpringDataQualificationExamScheduleRepository;
import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class CertificateRecommendationEnricher {

    private final SpringDataQualificationExamScheduleRepository scheduleRepository;

    public CertificateRecommendationEnricher(
            SpringDataQualificationExamScheduleRepository scheduleRepository
    ) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<CertificateRecommendation> enrich(
            List<CertificateRecommendation> certificates
    ) {
        return certificates.stream()
                .map(this::enrichOne)
                .toList();
    }

    private CertificateRecommendation enrichOne(
            CertificateRecommendation certificate
    ) {
        return scheduleRepository
                .findFirstByQualificationNameAndDocExamStartDateGreaterThanEqualOrderByDocExamStartDateAsc(
                        certificate.name(),
                        LocalDate.now()
                )
                .map(schedule -> withSchedule(certificate, schedule))
                .orElse(certificate);
    }

    private CertificateRecommendation withSchedule(
            CertificateRecommendation certificate,
            QualificationExamScheduleJpaEntity schedule
    ) {
        return new CertificateRecommendation(
                certificate.certificationId(),
                certificate.name(),
                certificate.reason(),
                certificate.relatedSkills(),
                certificate.difficulty(),
                schedule.getDocExamStartDate(),
                schedule.getDocRegStartDate(),
                schedule.getDocRegEndDate()
        );
    }
}