package com.sashimi.resume.infrastructure.persistence;

import com.sashimi.resume.domain.model.ResumeCertification;
import com.sashimi.resume.domain.model.ResumeCertificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "resume_certifications")
public class ResumeCertificationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_certification_id")
    private Long resumeCertificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "resume_id",
            nullable = false
    )
    private ResumeJpaEntity resume;

    @Column(
            name = "name",
            nullable = false
    )
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "type",
            nullable = false,
            length = 30
    )
    private ResumeCertificationType type;

    @Column(
            name = "issuer",
            nullable = false
    )
    private String issuer;

    @Column(
            name = "acquired_date",
            nullable = false
    )
    private LocalDate acquiredDate;

    @Column(name = "score_or_grade", length = 100)
    private String scoreOrGrade;

    @Column(
            name = "certification_order",
            nullable = false
    )
    private int certificationOrder;

    protected ResumeCertificationJpaEntity() {
    }

    private ResumeCertificationJpaEntity(
            ResumeJpaEntity resume,
            ResumeCertification certification,
            int certificationOrder
    ) {
        this.resume = resume;
        this.name = certification.name();
        this.type = certification.type();
        this.issuer = certification.issuer();
        this.acquiredDate = certification.acquiredDate();
        this.scoreOrGrade = certification.scoreOrGrade();
        this.certificationOrder = certificationOrder;
    }

    public static ResumeCertificationJpaEntity from(
            ResumeJpaEntity resume,
            ResumeCertification certification,
            int certificationOrder
    ) {
        return new ResumeCertificationJpaEntity(
                resume,
                certification,
                certificationOrder
        );
    }

    public ResumeCertification toDomain() {
        return new ResumeCertification(
                name,
                type,
                issuer,
                acquiredDate,
                scoreOrGrade
        );
    }
}