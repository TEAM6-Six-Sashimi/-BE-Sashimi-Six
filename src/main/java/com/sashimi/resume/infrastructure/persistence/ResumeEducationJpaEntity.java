package com.sashimi.resume.infrastructure.persistence;

import com.sashimi.resume.domain.model.EducationDegree;
import com.sashimi.resume.domain.model.GraduationStatus;
import com.sashimi.resume.domain.model.ResumeEducation;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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

import java.time.YearMonth;

@Entity
@Table(name = "resume_educations")
public class ResumeEducationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_education_id")
    private Long resumeEducationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "resume_id",
            nullable = false
    )
    private ResumeJpaEntity resume;

    @Column(
            name = "school_name",
            nullable = false
    )
    private String schoolName;

    @Convert(
            converter = YearMonthStringConverter.class
    )
    @Column(
            name = "start_year_month",
            nullable = false,
            length = 7
    )
    private YearMonth startYearMonth;

    @Convert(
            converter = YearMonthStringConverter.class
    )
    @Column(
            name = "end_year_month",
            nullable = false,
            length = 7
    )
    private YearMonth endYearMonth;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "degree",
            nullable = false,
            length = 30
    )
    private EducationDegree degree;

    @Column(
            name = "major",
            nullable = false
    )
    private String major;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "graduation_status",
            nullable = false,
            length = 30
    )
    private GraduationStatus graduationStatus;

    @Column(
            name = "minor_or_research",
            columnDefinition = "TEXT"
    )
    private String minorOrResearch;

    @Column(
            name = "education_order",
            nullable = false
    )
    private int educationOrder;

    protected ResumeEducationJpaEntity() {
    }

    private ResumeEducationJpaEntity(
            ResumeJpaEntity resume,
            ResumeEducation education,
            int educationOrder
    ) {
        this.resume = resume;
        this.schoolName = education.schoolName();
        this.startYearMonth =
                education.startYearMonth();
        this.endYearMonth =
                education.endYearMonth();
        this.degree = education.degree();
        this.major = education.major();
        this.graduationStatus =
                education.graduationStatus();
        this.minorOrResearch =
                education.minorOrResearch();
        this.educationOrder = educationOrder;
    }

    public static ResumeEducationJpaEntity from(
            ResumeJpaEntity resume,
            ResumeEducation education,
            int educationOrder
    ) {
        return new ResumeEducationJpaEntity(
                resume,
                education,
                educationOrder
        );
    }

    public ResumeEducation toDomain() {
        return new ResumeEducation(
                schoolName,
                startYearMonth,
                endYearMonth,
                graduationStatus,
                major,
                degree,
                minorOrResearch
        );
    }
}