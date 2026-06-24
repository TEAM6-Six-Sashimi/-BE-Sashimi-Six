package com.sashimi.instructorapplication.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class InstructorApplication {

    private Long id;
    private Long userId;
    private String bio;
    private String motivationLetter;
    private Long categoryId;
    private String portfolioUrl;
    private String profileImagePath;
    private String resumeFilePath;
    private List<String> mainCareers;
    private List<InstructorCertification> certifications;
    private ApprovalStatus approvalStatus;
    private RejectionCategory rejectionCategory;
    private String rejectionReason;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InstructorApplication create(
            Long userId, String bio, String motivationLetter, Long categoryId,
            String portfolioUrl, String profileImagePath, String resumeFilePath,
            List<String> mainCareers, List<InstructorCertification> certifications) {
        return InstructorApplication.builder()
                .userId(userId)
                .bio(bio)
                .motivationLetter(motivationLetter)
                .categoryId(categoryId)
                .portfolioUrl(portfolioUrl)
                .profileImagePath(profileImagePath)
                .resumeFilePath(resumeFilePath)
                .mainCareers(mainCareers)
                .certifications(certifications)
                .approvalStatus(ApprovalStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void approve() {
        if (this.approvalStatus != ApprovalStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_APPLICATION_STATUS);
        }
        this.approvalStatus = ApprovalStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject(RejectionCategory rejectionCategory, String rejectionReason) {
        if (this.approvalStatus != ApprovalStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_APPLICATION_STATUS);
        }
        this.approvalStatus = ApprovalStatus.REJECTED;
        this.rejectionCategory = rejectionCategory;
        this.rejectionReason = rejectionReason;
        this.updatedAt = LocalDateTime.now();
    }
}
