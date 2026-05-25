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
    @Column(name = "instructorProfileId")
    private Long id;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "career", columnDefinition = "TEXT")
    private String career;

    @Column(name = "portfolioUrl")
    private String portfolioUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "approvalStatus")
    private ApprovalStatus approvalStatus;

    @Column(name = "approvedAt")
    private LocalDateTime approvedAt;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Builder
    public InstructorApplicationJpaEntity(Long id, Long userId, String bio, String career,
                                          String portfolioUrl, ApprovalStatus approvalStatus,
                                          LocalDateTime approvedAt, LocalDateTime createdAt,
                                          LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.bio = bio;
        this.career = career;
        this.portfolioUrl = portfolioUrl;
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
                .career(domain.getCareer())
                .portfolioUrl(domain.getPortfolioUrl())
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
                .career(career)
                .portfolioUrl(portfolioUrl)
                .approvalStatus(approvalStatus)
                .approvedAt(approvedAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}