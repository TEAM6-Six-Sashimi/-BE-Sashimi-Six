package com.sashimi.instructorapplication.presentation.api.response;

import com.sashimi.instructorapplication.domain.model.ApprovalStatus;
import com.sashimi.instructorapplication.domain.model.InstructorApplication;
import com.sashimi.instructorapplication.domain.model.InstructorCertification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record InstructorApplicationResponse(
        Long id,
        Long userId,
        String bio,
        String portfolioUrl,
        List<InstructorApplicationDetailResponse.CertificationInfo> certifications,
        ApprovalStatus approvalStatus,
        LocalDateTime approvedAt,
        LocalDateTime createdAt
) {
    public static InstructorApplicationResponse from(InstructorApplication domain) {
        List<InstructorCertification> certs = domain.getCertifications() == null ? List.of() : domain.getCertifications();
        List<InstructorApplicationDetailResponse.CertificationInfo> certInfos = certs.stream()
                .map(c -> new InstructorApplicationDetailResponse.CertificationInfo(c.getCertificationName(), c.getIssuedBy(), null))
                .collect(Collectors.toList());

        return new InstructorApplicationResponse(
                domain.getId(),
                domain.getUserId(),
                domain.getBio(),
                domain.getPortfolioUrl(),
                certInfos,
                domain.getApprovalStatus(),
                domain.getApprovedAt(),
                domain.getCreatedAt()
        );
    }
}
