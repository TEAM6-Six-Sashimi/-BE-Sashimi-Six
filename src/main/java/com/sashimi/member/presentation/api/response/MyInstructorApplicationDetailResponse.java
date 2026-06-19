package com.sashimi.member.presentation.api.response;

import com.sashimi.member.domain.model.ApprovalStatus;
import com.sashimi.member.domain.model.InstructorApplication;
import com.sashimi.member.domain.model.RejectionCategory;
import com.sashimi.user.domain.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record MyInstructorApplicationDetailResponse(
        String userName,
        String loginId,
        String email,
        String phone,
        Long categoryId,
        LocalDateTime createdAt,
        ApprovalStatus approvalStatus,
        RejectionCategory rejectionCategory,
        String rejectionReason,
        LocalDateTime rejectedAt,
        String profileImagePath,
        String bio,
        String motivationLetter,
        String portfolioUrl,
        String resumeFilePath,
        List<String> mainCareers,
        List<CertificationInfo> certifications
) {
    public record CertificationInfo(String certificationName, String issuedBy) {}

    public static MyInstructorApplicationDetailResponse of(InstructorApplication application, User user) {
        List<CertificationInfo> certInfos = application.getCertifications() == null ? List.of() :
                application.getCertifications().stream()
                        .map(c -> new CertificationInfo(c.getCertificationName(), c.getIssuedBy()))
                        .collect(Collectors.toList());

        return new MyInstructorApplicationDetailResponse(
                user.getName(),
                user.getLoginId(),
                user.getEmail(),
                user.getPhone(),
                application.getCategoryId(),
                application.getCreatedAt(),
                application.getApprovalStatus(),
                application.getRejectionCategory(),
                application.getRejectionReason(),
                application.getUpdatedAt(),
                application.getProfileImagePath(),
                application.getBio(),
                application.getMotivationLetter(),
                application.getPortfolioUrl(),
                application.getResumeFilePath(),
                application.getMainCareers(),
                certInfos
        );
    }
}
