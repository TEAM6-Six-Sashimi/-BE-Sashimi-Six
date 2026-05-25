package com.sashimi.member.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InstructorApplication {

    private Long id;
    private Long userId;
    private String bio;
    private String career;
    private String portfolioUrl;
    private ApprovalStatus approvalStatus;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InstructorApplication create(Long userId, String bio, String career, String portfolioUrl) {
        return InstructorApplication.builder()
                .userId(userId)
                .bio(bio)
                .career(career)
                .portfolioUrl(portfolioUrl)
                .approvalStatus(ApprovalStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void approve() {
        this.approvalStatus = ApprovalStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject() {
        this.approvalStatus = ApprovalStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }
}