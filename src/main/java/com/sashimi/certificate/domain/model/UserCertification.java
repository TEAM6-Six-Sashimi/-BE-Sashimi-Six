package com.sashimi.certificate.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class UserCertification {

    private Long id;
    private Long userId;
    private String certificationName;
    private String issuedBy;
    private LocalDate issuedDate;
    private String fileName;
    private CertificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public static UserCertification create(Long userId, String certificationName,
                                           String issuedBy, LocalDate issuedDate,
                                           String fileName) {
        return UserCertification.builder()
                .userId(userId)
                .certificationName(certificationName)
                .issuedBy(issuedBy)
                .issuedDate(issuedDate)
                .fileName(fileName)
                .status(CertificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void verify() {
        this.status = CertificationStatus.VERIFIED;
    }

    public void reject() {
        this.status = CertificationStatus.REJECTED;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
