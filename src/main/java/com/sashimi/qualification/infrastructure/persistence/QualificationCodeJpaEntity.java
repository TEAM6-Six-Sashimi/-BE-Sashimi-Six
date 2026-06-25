package com.sashimi.qualification.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "qualification_codes")
public class QualificationCodeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qualification_code_id")
    private Long id;

    @Column(name = "jm_cd", nullable = false, length = 20)
    private String jmCd;

    @Column(name = "qualification_name", nullable = false)
    private String qualificationName;

    @Column(name = "qualification_type_code", nullable = false, length = 10)
    private String qualificationTypeCode;

    @Column(name = "qualification_type_name", nullable = false, length = 100)
    private String qualificationTypeName;

    @Column(name = "series_code", length = 20)
    private String seriesCode;

    @Column(name = "series_name", length = 100)
    private String seriesName;

    @Column(name = "major_field_code", length = 20)
    private String majorFieldCode;

    @Column(name = "major_field_name", length = 100)
    private String majorFieldName;

    @Column(name = "middle_field_code", length = 20)
    private String middleFieldCode;

    @Column(name = "middle_field_name", length = 100)
    private String middleFieldName;

    @Column(name = "last_synced_at", nullable = false)
    private LocalDateTime lastSyncedAt;

    protected QualificationCodeJpaEntity() {
    }

    public QualificationCodeJpaEntity(
            String jmCd,
            String qualificationName,
            String qualificationTypeCode,
            String qualificationTypeName,
            String seriesCode,
            String seriesName,
            String majorFieldCode,
            String majorFieldName,
            String middleFieldCode,
            String middleFieldName,
            LocalDateTime lastSyncedAt
    ) {
        this.jmCd = jmCd;
        this.qualificationName = qualificationName;
        this.qualificationTypeCode = qualificationTypeCode;
        this.qualificationTypeName = qualificationTypeName;
        this.seriesCode = seriesCode;
        this.seriesName = seriesName;
        this.majorFieldCode = majorFieldCode;
        this.majorFieldName = majorFieldName;
        this.middleFieldCode = middleFieldCode;
        this.middleFieldName = middleFieldName;
        this.lastSyncedAt = lastSyncedAt;
    }

    public void updateFromSync(
            String qualificationName,
            String qualificationTypeCode,
            String qualificationTypeName,
            String seriesCode,
            String seriesName,
            String majorFieldCode,
            String majorFieldName,
            String middleFieldCode,
            String middleFieldName,
            LocalDateTime lastSyncedAt
    ) {
        this.qualificationName = qualificationName;
        this.qualificationTypeCode = qualificationTypeCode;
        this.qualificationTypeName = qualificationTypeName;
        this.seriesCode = seriesCode;
        this.seriesName = seriesName;
        this.majorFieldCode = majorFieldCode;
        this.majorFieldName = majorFieldName;
        this.middleFieldCode = middleFieldCode;
        this.middleFieldName = middleFieldName;
        this.lastSyncedAt = lastSyncedAt;
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

    public String getQualificationTypeCode() {
        return qualificationTypeCode;
    }

    public String getQualificationTypeName() {
        return qualificationTypeName;
    }

    public String getSeriesCode() {
        return seriesCode;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public String getMajorFieldCode() {
        return majorFieldCode;
    }

    public String getMajorFieldName() {
        return majorFieldName;
    }

    public String getMiddleFieldCode() {
        return middleFieldCode;
    }

    public String getMiddleFieldName() {
        return middleFieldName;
    }
}