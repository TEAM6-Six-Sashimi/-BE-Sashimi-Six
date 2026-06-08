package com.sashimi.member.presentation.api.response;

import com.sashimi.member.domain.model.CertificationStatus;
import com.sashimi.member.domain.model.UserCertification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CertificateResponse(
        Long id,
        Long userId,
        String certificationName,
        String issuedBy,
        LocalDate issuedDate,
        String fileName,
        CertificationStatus status,
        LocalDateTime createdAt
) {
    public static CertificateResponse from(UserCertification domain) {
        return new CertificateResponse(
                domain.getId(),
                domain.getUserId(),
                domain.getCertificationName(),
                domain.getIssuedBy(),
                domain.getIssuedDate(),
                domain.getFileName(),
                domain.getStatus(),
                domain.getCreatedAt()
        );
    }
}
