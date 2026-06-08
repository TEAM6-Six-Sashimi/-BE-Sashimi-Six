package com.sashimi.member.domain.model;

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
    private String portfolioUrl;
    private List<InstructorCertification> certifications;
    private ApprovalStatus approvalStatus;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InstructorApplication create(Long userId, String bio, String portfolioUrl, List<InstructorCertification> certifications) {
        return InstructorApplication.builder()
                .userId(userId)
                .bio(bio)
                .portfolioUrl(portfolioUrl)
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

    public void reject() {
        if (this.approvalStatus != ApprovalStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_APPLICATION_STATUS);
        }
        this.approvalStatus = ApprovalStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }
}