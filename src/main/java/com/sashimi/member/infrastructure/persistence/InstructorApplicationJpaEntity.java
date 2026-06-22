package com.sashimi.member.infrastructure.persistence;

import com.sashimi.member.domain.model.ApprovalStatus;
import com.sashimi.member.domain.model.InstructorApplication;
import com.sashimi.member.domain.model.InstructorCertification;
import com.sashimi.member.domain.model.RejectionCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Column(name = "motivation_letter", columnDefinition = "TEXT")
    private String motivationLetter;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "portfolio_url")
    private String portfolioUrl;

    @Column(name = "profile_image_path", length = 500)
    private String profileImagePath;

    @Column(name = "resume_file_path", length = 500)
    private String resumeFilePath;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "main_careers", columnDefinition = "JSON")
    private List<String> mainCareers;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<InstructorCertificationJpaEntity> certifications = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "rejection_category")
    private RejectionCategory rejectionCategory;

    @Column(name = "rejection_reason", length = 100)
    private String rejectionReason;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public InstructorApplicationJpaEntity(Long id, Long userId, String bio, String motivationLetter,
                                          Long categoryId, String portfolioUrl,
                                          String profileImagePath, String resumeFilePath,
                                          List<String> mainCareers,
                                          ApprovalStatus approvalStatus,
                                          RejectionCategory rejectionCategory, String rejectionReason,
                                          LocalDateTime approvedAt, LocalDateTime createdAt,
                                          LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.bio = bio;
        this.motivationLetter = motivationLetter;
        this.categoryId = categoryId;
        this.portfolioUrl = portfolioUrl;
        this.profileImagePath = profileImagePath;
        this.resumeFilePath = resumeFilePath;
        this.mainCareers = mainCareers;
        this.approvalStatus = approvalStatus;
        this.rejectionCategory = rejectionCategory;
        this.rejectionReason = rejectionReason;
        this.approvedAt = approvedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static InstructorApplicationJpaEntity from(InstructorApplication domain) {
        InstructorApplicationJpaEntity entity = InstructorApplicationJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .bio(domain.getBio())
                .motivationLetter(domain.getMotivationLetter())
                .categoryId(domain.getCategoryId())
                .portfolioUrl(domain.getPortfolioUrl())
                .profileImagePath(domain.getProfileImagePath())
                .resumeFilePath(domain.getResumeFilePath())
                .mainCareers(domain.getMainCareers())
                .approvalStatus(domain.getApprovalStatus())
                .rejectionCategory(domain.getRejectionCategory())
                .rejectionReason(domain.getRejectionReason())
                .approvedAt(domain.getApprovedAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();

        if (domain.getCertifications() != null) {
            domain.getCertifications().forEach(cert ->
                    entity.certifications.add(InstructorCertificationJpaEntity.builder()
                            .certificationName(cert.getCertificationName())
                            .issuedBy(cert.getIssuedBy())
                            .filePath(cert.getFilePath())
                            .application(entity)
                            .build())
            );
        }
        return entity;
    }

    public InstructorApplication toDomain() {
        List<InstructorCertification> certDomains = certifications.stream()
                .map(InstructorCertificationJpaEntity::toDomain)
                .collect(Collectors.toList());

        return InstructorApplication.builder()
                .id(id)
                .userId(userId)
                .bio(bio)
                .motivationLetter(motivationLetter)
                .categoryId(categoryId)
                .portfolioUrl(portfolioUrl)
                .profileImagePath(profileImagePath)
                .resumeFilePath(resumeFilePath)
                .mainCareers(mainCareers)
                .certifications(certDomains)
                .approvalStatus(approvalStatus)
                .rejectionCategory(rejectionCategory)
                .rejectionReason(rejectionReason)
                .approvedAt(approvedAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}

