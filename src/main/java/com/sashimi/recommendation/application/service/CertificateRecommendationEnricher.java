package com.sashimi.recommendation.application.service;

import com.sashimi.qualification.application.service.QualificationCodeSyncService;
import com.sashimi.qualification.application.service.QualificationExamScheduleSyncService;
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
    private final QualificationCodeSyncService qualificationCodeSyncService;
    private final QualificationExamScheduleSyncService scheduleSyncService;

    public CertificateRecommendationEnricher(
            SpringDataQualificationExamScheduleRepository scheduleRepository,
            SpringDataQualificationCodeRepository qualificationCodeRepository,
            QualificationCodeSyncService qualificationCodeSyncService,
            QualificationExamScheduleSyncService scheduleSyncService
    ) {
        this.scheduleRepository = scheduleRepository;
        this.qualificationCodeRepository = qualificationCodeRepository;
        this.qualificationCodeSyncService = qualificationCodeSyncService;
        this.scheduleSyncService = scheduleSyncService;
    }

    public List<CertificateRecommendation> enrich(
            List<CertificateRecommendation> certificates
    ) {
        if (certificates == null || certificates.isEmpty()) {
            return List.of();
        }

        return certificates.stream()
                .map(this::enrichOneSafely)
                .toList();
    }

    private CertificateRecommendation enrichOneSafely(
            CertificateRecommendation certificate
    ) {
        try {
            return enrichOne(certificate);
        } catch (Exception e) {
            return certificate;
        }
    }

    private CertificateRecommendation enrichOne(
            CertificateRecommendation certificate
    ) {
        if (certificate.name() == null || certificate.name().isBlank()) {
            return certificate;
        }

        String lookupName = certificate.name().trim();

        Optional<QualificationCodeJpaEntity> qualificationCode =
                findQualificationCode(lookupName);

        if (qualificationCode.isEmpty()) {
            syncQualificationCodesSafely();
            qualificationCode = findQualificationCode(lookupName);
        }

        if (qualificationCode.isEmpty()) {
            return certificate;
        }

        QualificationCodeJpaEntity code = qualificationCode.get();

        Optional<QualificationExamScheduleJpaEntity> schedule =
                findNextSchedule(code.getJmCd());

        if (schedule.isEmpty()) {
            syncScheduleSafely(lookupName);
            schedule = findNextSchedule(code.getJmCd());
        }

        return schedule
                .map(foundSchedule -> withCodeAndSchedule(certificate, code, foundSchedule))
                .orElseGet(() -> withCodeOnly(certificate, code));
    }

    private Optional<QualificationCodeJpaEntity> findQualificationCode(
            String lookupName
    ) {
        return qualificationCodeRepository.findFirstByQualificationNameOrderByJmCdAsc(
                lookupName
        ).or(() ->
                qualificationCodeRepository
                        .findFirstByQualificationNameContainingOrderByQualificationNameAsc(
                                lookupName
                        )
        );
    }

    private Optional<QualificationExamScheduleJpaEntity> findNextSchedule(
            String jmCd
    ) {
        return scheduleRepository
                .findFirstByJmCdAndDocExamStartDateGreaterThanEqualOrderByDocExamStartDateAsc(
                        jmCd,
                        LocalDate.now()
                );
    }

    private void syncQualificationCodesSafely() {
        try {
            qualificationCodeSyncService.sync();
        } catch (Exception e) {
            // 외부 API 실패해도 추천 전체는 실패시키지 않음
        }
    }

    private void syncScheduleSafely(
            String qualificationName
    ) {
        try {
            int currentYear = LocalDate.now().getYear();
            scheduleSyncService.syncByQualificationName(
                    qualificationName,
                    currentYear
            );
        } catch (Exception e) {
            // 외부 API 실패해도 추천 전체는 실패시키지 않음
        }
    }

    private CertificateRecommendation withCodeAndSchedule(
            CertificateRecommendation certificate,
            QualificationCodeJpaEntity code,
            QualificationExamScheduleJpaEntity schedule
    ) {
        return new CertificateRecommendation(
                code.getId(),
                certificate.name(),
                certificate.reason(),
                certificate.relatedSkills(),
                certificate.difficulty(),
                schedule.getDocExamStartDate(),
                schedule.getDocRegStartDate(),
                schedule.getDocRegEndDate()
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
                null,
                null,
                null
        );
    }
}