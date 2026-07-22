package com.sashimi.instructorapplication.presentation.api.response;

import com.sashimi.instructorapplication.domain.model.InstructorApplication;
import com.sashimi.instructorapplication.domain.model.VerificationStatus;
import com.sashimi.user.domain.model.User;

import java.time.LocalDateTime;

public record InstructorApplicationListResponse(
        Long applicationId,
        String name,
        String loginId,
        String email,
        String categoryName,
        LocalDateTime createdAt,
        CertificationSubmissionStatus verificationStatus
) {
    public static InstructorApplicationListResponse of(
            InstructorApplication application, User user, String categoryName, VerificationStatus verificationStatus) {
        return new InstructorApplicationListResponse(
                application.getId(),
                user.getName(),
                user.getLoginId(),
                user.getEmail(),
                categoryName,
                application.getCreatedAt(),
                CertificationSubmissionStatus.from(verificationStatus)
        );
    }
}
