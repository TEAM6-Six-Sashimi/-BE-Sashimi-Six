package com.sashimi.resume.infrastructure.persistence;

import com.sashimi.resume.domain.model.EmploymentType;
import com.sashimi.resume.domain.model.ResumeCareer;
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
@Table(name = "resume_careers")
public class ResumeCareerJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_career_id")
    private Long resumeCareerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "resume_id",
            nullable = false
    )
    private ResumeJpaEntity resume;

    @Column(
            name = "company_name",
            nullable = false
    )
    private String companyName;

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
            length = 7
    )
    private YearMonth endYearMonth;

    @Column(
            name = "currently_employed",
            nullable = false
    )
    private boolean currentlyEmployed;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "employment_type",
            nullable = false,
            length = 30
    )
    private EmploymentType employmentType;

    @Column(name = "custom_employment_type")
    private String customEmploymentType;

    @Column(
            name = "job_title",
            nullable = false
    )
    private String jobTitle;

    @Column(
            name = "career_order",
            nullable = false
    )
    private int careerOrder;

    protected ResumeCareerJpaEntity() {
    }

    private ResumeCareerJpaEntity(
            ResumeJpaEntity resume,
            ResumeCareer career,
            int careerOrder
    ) {
        this.resume = resume;
        this.companyName = career.companyName();
        this.startYearMonth =
                career.startYearMonth();
        this.endYearMonth =
                career.endYearMonth();
        this.currentlyEmployed =
                career.currentlyEmployed();
        this.employmentType =
                career.employmentType();
        this.customEmploymentType =
                career.customEmploymentType();
        this.jobTitle = career.jobTitle();
        this.careerOrder = careerOrder;
    }

    public static ResumeCareerJpaEntity from(
            ResumeJpaEntity resume,
            ResumeCareer career,
            int careerOrder
    ) {
        return new ResumeCareerJpaEntity(
                resume,
                career,
                careerOrder
        );
    }

    public ResumeCareer toDomain() {
        return new ResumeCareer(
                companyName,
                startYearMonth,
                endYearMonth,
                currentlyEmployed,
                employmentType,
                customEmploymentType,
                jobTitle
        );
    }
}