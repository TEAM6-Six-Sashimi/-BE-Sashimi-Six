package com.sashimi.instructorapplication.infrastructure.persistence;

import com.sashimi.instructorapplication.domain.model.InstructorCertification;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "instructor_application_certifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstructorCertificationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certification_id")
    private Long id;

    @Column(name = "certification_name", nullable = false)
    private String certificationName;

    @Column(name = "issued_by")
    private String issuedBy;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_profile_id", nullable = false)
    private InstructorApplicationJpaEntity application;

    @Builder
    public InstructorCertificationJpaEntity(String certificationName, String issuedBy, String filePath, InstructorApplicationJpaEntity application) {
        this.certificationName = certificationName;
        this.issuedBy = issuedBy;
        this.filePath = filePath;
        this.application = application;
    }

    public InstructorCertification toDomain() {
        return InstructorCertification.builder()
                .id(id)
                .certificationName(certificationName)
                .issuedBy(issuedBy)
                .filePath(filePath)
                .build();
    }
}
