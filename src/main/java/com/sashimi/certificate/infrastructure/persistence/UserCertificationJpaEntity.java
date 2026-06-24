package com.sashimi.certificate.infrastructure.persistence;

import com.sashimi.certificate.domain.model.CertificationStatus;
import com.sashimi.certificate.domain.model.UserCertification;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_certifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCertificationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_certification_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "certification_name", nullable = false)
    private String certificationName;

    @Column(name = "issued_by")
    private String issuedBy;

    @Column(name = "issued_date")
    private LocalDate issuedDate;

    @Column(name = "file_name")
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CertificationStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public UserCertificationJpaEntity(Long id, Long userId, String certificationName,
                                      String issuedBy, LocalDate issuedDate, String fileName,
                                      CertificationStatus status, LocalDateTime createdAt,
                                      LocalDateTime deletedAt) {
        this.id = id;
        this.userId = userId;
        this.certificationName = certificationName;
        this.issuedBy = issuedBy;
        this.issuedDate = issuedDate;
        this.fileName = fileName;
        this.status = status;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public static UserCertificationJpaEntity from(UserCertification domain) {
        return UserCertificationJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .certificationName(domain.getCertificationName())
                .issuedBy(domain.getIssuedBy())
                .issuedDate(domain.getIssuedDate())
                .fileName(domain.getFileName())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }

    public UserCertification toDomain() {
        return UserCertification.builder()
                .id(id)
                .userId(userId)
                .certificationName(certificationName)
                .issuedBy(issuedBy)
                .issuedDate(issuedDate)
                .fileName(fileName)
                .status(status)
                .createdAt(createdAt)
                .deletedAt(deletedAt)
                .build();
    }
}
