package com.sashimi.instructorapplication.presentation.api.response;

import com.sashimi.instructorapplication.domain.model.ApprovalStatus;
import com.sashimi.instructorapplication.domain.model.InstructorApplication;
import com.sashimi.instructorapplication.domain.model.InstructorCertification;
import com.sashimi.instructorapplication.domain.model.RejectionCategory;
import com.sashimi.user.domain.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    public record CertificationInfo(String fileUrl) {}

    public static MyInstructorApplicationDetailResponse of(
            InstructorApplication application,
            User user,
            String profileImageUrl,
            String resumeFileUrl,
            List<String> certFileUrls) {

        List<CertificationInfo> certInfos = new ArrayList<>();
        List<InstructorCertification> certs =
                application.getCertifications() == null ? List.of() : application.getCertifications();
        for (int i = 0; i < certs.size(); i++) {
            String fileUrl = (certFileUrls != null && i < certFileUrls.size()) ? certFileUrls.get(i) : null;
            certInfos.add(new CertificationInfo(fileUrl));
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
