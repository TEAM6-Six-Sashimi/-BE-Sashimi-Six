package com.sashimi.member.presentation.api.response;

import com.sashimi.member.domain.model.ApprovalStatus;
import com.sashimi.member.domain.model.InstructorApplication;

import java.time.LocalDateTime;

public record InstructorApplicationResponse(
        Long id,
        Long userId,
        String bio,
        String career,
        String portfolioUrl,
        ApprovalStatus approvalStatus,
        LocalDateTime approvedAt,
        LocalDateTime createdAt
) {
    public static InstructorApplicationResponse from(InstructorApplication domain) {
        return new InstructorApplicationResponse(
                domain.getId(),
                domain.getUserId(),
                domain.getBio(),
                domain.getCareer(),
                domain.getPortfolioUrl(),
                domain.getApprovalStatus(),
                domain.getApprovedAt(),
                domain.getCreatedAt()
        );
    }
}