package com.sashimi.member.presentation.api.response;

import com.sashimi.member.domain.model.ApprovalStatus;
import com.sashimi.member.domain.model.InstructorApplication;

import java.time.LocalDateTime;

public record InstructorApplicationDetailResponse(
        String bio,
        String portfolioUrl,
        String certificationName,
        String issuedBy,
        ApprovalStatus approvalStatus,
        LocalDateTime approvedAt,
        LocalDateTime createdAt
) {
    public static InstructorApplicationDetailResponse from(InstructorApplication application) {
        return new InstructorApplicationDetailResponse(
                application.getBio(),
                application.getPortfolioUrl(),
                application.getCertificationName(),
                application.getIssuedBy(),
                application.getApprovalStatus(),
                application.getApprovedAt(),
                application.getCreatedAt()
        );
    }
}
