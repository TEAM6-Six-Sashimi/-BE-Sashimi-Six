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
        String profileImageUrl,
        String bio,
        String motivationLetter,
        String portfolioUrl,
        String resumeFileUrl,
        List<String> mainCareers,
        List<CertificationInfo> certifications
) {
    public record CertificationInfo(String certificationName, String issuedBy, String fileUrl) {}

    public static MyInstructorApplicationDetailResponse of(
            InstructorApplication application,
            User user,
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
                profileImageUrl,
                application.getBio(),
                application.getMotivationLetter(),
                application.getPortfolioUrl(),
                resumeFileUrl,
                application.getMainCareers(),
                certInfos
        );
    }
}
