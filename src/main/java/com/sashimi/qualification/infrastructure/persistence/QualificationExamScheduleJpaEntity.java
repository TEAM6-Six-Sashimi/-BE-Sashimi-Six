package com.sashimi.qualification.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "qualification_exam_schedules")
public class QualificationExamScheduleJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qualification_exam_schedule_id")
    private Long id;

    @Column(name = "jm_cd", length = 20)
    private String jmCd;

    @Column(name = "qualification_name")
    private String qualificationName;

    @Column(name = "impl_year", nullable = false)
    private int implYear;

    @Column(name = "impl_seq", nullable = false)
    private int implSeq;

    @Column(name = "qualification_type_code", nullable = false, length = 10)
    private String qualificationTypeCode;

    @Column(name = "qualification_type_name", nullable = false, length = 100)
    private String qualificationTypeName;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "doc_reg_start_date")
    private LocalDate docRegStartDate;

    @Column(name = "doc_reg_end_date")
    private LocalDate docRegEndDate;

    @Column(name = "doc_exam_start_date")
    private LocalDate docExamStartDate;

    @Column(name = "doc_exam_end_date")
    private LocalDate docExamEndDate;

    @Column(name = "last_synced_at", nullable = false)
    private LocalDateTime lastSyncedAt;

    protected QualificationExamScheduleJpaEntity() {
    }

    public QualificationExamScheduleJpaEntity(
            String jmCd,
            String qualificationName,
            int implYear,
            int implSeq,
            String qualificationTypeCode,
            String qualificationTypeName,
            String description,
            LocalDate docRegStartDate,
            LocalDate docRegEndDate,
            LocalDate docExamStartDate,
            LocalDate docExamEndDate,
            LocalDateTime lastSyncedAt
    ) {
        this.jmCd = jmCd;
        this.qualificationName = qualificationName;
        this.implYear = implYear;
        this.implSeq = implSeq;
        this.qualificationTypeCode = qualificationTypeCode;
        this.qualificationTypeName = qualificationTypeName;
        this.description = description;
        this.docRegStartDate = docRegStartDate;
        this.docRegEndDate = docRegEndDate;
        this.docExamStartDate = docExamStartDate;
        this.docExamEndDate = docExamEndDate;
        this.lastSyncedAt = lastSyncedAt;
    }

    public void updateFromSync(
            LocalDate docRegStartDate,
            LocalDate docRegEndDate,
            LocalDate docExamStartDate,
            LocalDate docExamEndDate,
            LocalDateTime lastSyncedAt
    ) {
        this.docRegStartDate = docRegStartDate;
        this.docRegEndDate = docRegEndDate;
        this.docExamStartDate = docExamStartDate;
        this.docExamEndDate = docExamEndDate;
        this.lastSyncedAt = lastSyncedAt;
    }

    public void updateQualificationInfo(
            String jmCd,
            String qualificationName
    ) {
        this.jmCd = jmCd;
        this.qualificationName = qualificationName;
    }

    public Long getId() {
        return id;
    }

    public String getJmCd() {
        return jmCd;
    }

    public String getQualificationName() {
        return qualificationName;
    }

    public int getImplYear() {
        return implYear;
    }

    public int getImplSeq() {
        return implSeq;
    }

    public String getQualificationTypeCode() {
        return qualificationTypeCode;
    }

    public String getQualificationTypeName() {
        return qualificationTypeName;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDocRegStartDate() {
        return docRegStartDate;
    }

    public LocalDate getDocRegEndDate() {
        return docRegEndDate;
    }

    public LocalDate getDocExamStartDate() {
        return docExamStartDate;
    }

    public LocalDate getDocExamEndDate() {
        return docExamEndDate;
    }
}