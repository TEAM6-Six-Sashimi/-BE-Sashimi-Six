package com.sashimi.member.presentation.api.response;

import com.sashimi.member.domain.model.ApprovalStatus;
import com.sashimi.member.domain.model.InstructorApplication;

import java.time.LocalDateTime;

public record InstructorApplicationResponse(
        Long id,
        Long userId,
        String bio,
        String portfolioUrl,
        String certificationName,
        String issuedBy,
        ApprovalStatus approvalStatus,
        LocalDateTime approvedAt,
        LocalDateTime createdAt
) {
    public static InstructorApplicationResponse from(InstructorApplication domain) {
        return new InstructorApplicationResponse(
                domain.getId(),
                domain.getUserId(),
                domain.getBio(),
                domain.getPortfolioUrl(),
                domain.getCertificationName(),
                domain.getIssuedBy(),
                domain.getApprovalStatus(),
                domain.getApprovedAt(),
                domain.getCreatedAt()
        );
    }
}