package com.sashimi.recommendation.application.service;

import com.sashimi.qualification.infrastructure.persistence.QualificationCodeJpaEntity;
import com.sashimi.qualification.infrastructure.persistence.QualificationExamScheduleJpaEntity;
import com.sashimi.qualification.infrastructure.persistence.SpringDataQualificationCodeRepository;
import com.sashimi.qualification.infrastructure.persistence.SpringDataQualificationExamScheduleRepository;
import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class CertificateRecommendationEnricher {

    private final SpringDataQualificationExamScheduleRepository scheduleRepository;
    private final SpringDataQualificationCodeRepository qualificationCodeRepository;

    public CertificateRecommendationEnricher(
            SpringDataQualificationExamScheduleRepository scheduleRepository,
            SpringDataQualificationCodeRepository qualificationCodeRepository
    ) {
        this.scheduleRepository = scheduleRepository;
        this.qualificationCodeRepository = qualificationCodeRepository;
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
        Optional<QualificationCodeJpaEntity> qualificationCode =
                qualificationCodeRepository.findFirstByQualificationNameOrderByJmCdAsc(
                        certificate.name()
                );

        if (qualificationCode.isEmpty()) {
            return certificate;
        }

        QualificationCodeJpaEntity code = qualificationCode.get();

        return scheduleRepository
                .findFirstByJmCdAndDocExamStartDateGreaterThanEqualOrderByDocExamStartDateAsc(
                        code.getJmCd(),
                        LocalDate.now()
                )
                .map(schedule -> withCodeAndSchedule(certificate, code, schedule))
                .orElseGet(() -> withCodeOnly(certificate, code));
    }

   private CertificateRecommendation withCodeOnly(
        CertificateRecommendation certificate,
        QualificationCodeJpaEntity code
) {
    return new CertificateRecommendation(
            code.getId(),
            certificate.name(),
            certificate.reason(),
            certificate.relatedSkills(),
            certificate.difficulty(),
            null,
            null,
            null
    );
}

    private CertificateRecommendation withCodeOnly(
            CertificateRecommendation certificate,
            QualificationCodeJpaEntity code
    ) {
        return new CertificateRecommendation(
                code.getId(),
                certificate.name(),
                certificate.reason(),
                certificate.relatedSkills(),
                certificate.difficulty(),
                certificate.nextExamDate(),
                certificate.applicationStartDate(),
                certificate.applicationEndDate()
        );
    }
}
