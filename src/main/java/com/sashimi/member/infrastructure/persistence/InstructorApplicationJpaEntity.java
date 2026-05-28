package com.sashimi.member.infrastructure.persistence;

import com.sashimi.member.domain.model.ApprovalStatus;
import com.sashimi.member.domain.model.InstructorApplication;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "instructor_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstructorApplicationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instructor_profile_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "portfolio_url")
    private String portfolioUrl;

    @Column(name = "certification_name")
    private String certificationName;

    @Column(name = "issued_by")
    private String issuedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public InstructorApplicationJpaEntity(Long id, Long userId, String bio,
                                          String portfolioUrl, String certificationName,
                                          String issuedBy, ApprovalStatus approvalStatus,
                                          LocalDateTime approvedAt, LocalDateTime createdAt,
                                          LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.bio = bio;
        this.portfolioUrl = portfolioUrl;
        this.certificationName = certificationName;
        this.issuedBy = issuedBy;
        this.approvalStatus = approvalStatus;
        this.approvedAt = approvedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static InstructorApplicationJpaEntity from(InstructorApplication domain) {
        return InstructorApplicationJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .bio(domain.getBio())
                .portfolioUrl(domain.getPortfolioUrl())
                .certificationName(domain.getCertificationName())
                .issuedBy(domain.getIssuedBy())
                .approvalStatus(domain.getApprovalStatus())
                .approvedAt(domain.getApprovedAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public InstructorApplication toDomain() {
        return InstructorApplication.builder()
                .id(id)
                .userId(userId)
                .bio(bio)
                .portfolioUrl(portfolioUrl)
                .certificationName(certificationName)
                .issuedBy(issuedBy)
                .approvalStatus(approvalStatus)
                .approvedAt(approvedAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}