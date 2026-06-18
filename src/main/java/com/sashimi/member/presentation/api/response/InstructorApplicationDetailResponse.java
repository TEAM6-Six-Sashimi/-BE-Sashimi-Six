package com.sashimi.member.presentation.api.response;

import com.sashimi.member.domain.model.ApprovalStatus;
import com.sashimi.member.domain.model.InstructorApplication;
import com.sashimi.member.domain.model.RejectionCategory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record InstructorApplicationDetailResponse(
        String bio,
        String motivationLetter,
        Long categoryId,
        String portfolioUrl,
        String profileImagePath,
        String resumeFilePath,
        List<String> mainCareers,
        List<CertificationInfo> certifications,
        ApprovalStatus approvalStatus,
        RejectionCategory rejectionCategory,
        String rejectionReason,
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
                application.getMotivationLetter(),
                application.getCategoryId(),
                application.getPortfolioUrl(),
                application.getProfileImagePath(),
                application.getResumeFilePath(),
                application.getMainCareers(),
                certInfos,
                application.getApprovalStatus(),
                application.getRejectionCategory(),
                application.getRejectionReason(),
                application.getApprovedAt(),
                application.getCreatedAt()
        );
    }
}
