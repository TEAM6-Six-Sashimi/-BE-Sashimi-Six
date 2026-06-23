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
        String profileImageUrl,
        String resumeFileUrl,
        List<String> mainCareers,
        List<CertificationInfo> certifications,
        ApprovalStatus approvalStatus,
        RejectionCategory rejectionCategory,
        String rejectionReason,
        LocalDateTime approvedAt,
        LocalDateTime createdAt
) {
    public record CertificationInfo(String certificationName, String issuedBy, String fileUrl) {}

    public static InstructorApplicationDetailResponse from(
            InstructorApplication application,
            String profileImageUrl,
            String resumeFileUrl,
            List<String> certFileUrls) {

        List<CertificationInfo> certInfos = new java.util.ArrayList<>();
        List<com.sashimi.member.domain.model.InstructorCertification> certs =
                application.getCertifications() == null ? List.of() : application.getCertifications();
        for (int i = 0; i < certs.size(); i++) {
            var c = certs.get(i);
            String fileUrl = (certFileUrls != null && i < certFileUrls.size()) ? certFileUrls.get(i) : null;
            certInfos.add(new CertificationInfo(c.getCertificationName(), c.getIssuedBy(), fileUrl));
        }

        return new InstructorApplicationDetailResponse(
                application.getBio(),
                application.getMotivationLetter(),
                application.getCategoryId(),
                application.getPortfolioUrl(),
                profileImageUrl,
                resumeFileUrl,
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
