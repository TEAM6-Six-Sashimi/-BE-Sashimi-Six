package com.sashimi.member.infrastructure.persistence;

import com.sashimi.member.domain.model.InstructorCertification;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_profile_id", nullable = false)
    private InstructorApplicationJpaEntity application;

    @Builder
    public InstructorCertificationJpaEntity(String certificationName, String issuedBy, InstructorApplicationJpaEntity application) {
        this.certificationName = certificationName;
        this.issuedBy = issuedBy;
        this.application = application;
    }

    public InstructorCertification toDomain() {
        return InstructorCertification.builder()
                .id(id)
                .certificationName(certificationName)
                .issuedBy(issuedBy)
                .build();
    }
}
