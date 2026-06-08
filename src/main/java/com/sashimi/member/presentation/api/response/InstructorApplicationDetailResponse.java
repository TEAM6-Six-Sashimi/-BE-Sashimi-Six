package com.sashimi.member.presentation.api.response;

import com.sashimi.member.domain.model.ApprovalStatus;
import com.sashimi.member.domain.model.InstructorApplication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record InstructorApplicationDetailResponse(
        String bio,
        String portfolioUrl,
        List<CertificationInfo> certifications,
        ApprovalStatus approvalStatus,
        LocalDateTime approvedAt,
        LocalDateTime createdAt
) {
    public record CertificationInfo(String certificationName, String issuedBy) {}

    public static InstructorApplicationDetailResponse from(InstructorApplication application) {
        List<CertificationInfo> certInfos = application.getCertifications().stream()
                .map(c -> new CertificationInfo(c.getCertificationName(), c.getIssuedBy()))
                .collect(Collectors.toList());

        return new InstructorApplicationDetailResponse(
                application.getBio(),
                application.getPortfolioUrl(),
                certInfos,
                application.getApprovalStatus(),
                application.getApprovedAt(),
                application.getCreatedAt()
        );
    }
}
