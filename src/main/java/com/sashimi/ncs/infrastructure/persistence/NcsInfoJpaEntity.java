package com.sashimi.ncs.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ncs_info")
public class NcsInfoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ncs_info_id")
    private Long id;

    @Column(name = "ncs_code", nullable = false, unique = true, length = 50)
    private String ncsCode;

    @Column(name = "category_path", length = 500)
    private String categoryPath;

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "ability_unit_code", length = 50)
    private String abilityUnitCode;

    @Column(name = "ability_unit_name")
    private String abilityUnitName;

    @Column(name = "ability_unit_description", columnDefinition = "TEXT")
    private String abilityUnitDescription;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    protected NcsInfoJpaEntity() {}

    public NcsInfoJpaEntity(String ncsCode, String categoryPath, String jobName,
                            String jobDescription, String abilityUnitCode, String abilityUnitName,
                            String abilityUnitDescription, LocalDateTime lastSyncedAt) {
        this.ncsCode = ncsCode;
        this.categoryPath = categoryPath;
        this.jobName = jobName;
        this.jobDescription = jobDescription;
        this.abilityUnitCode = abilityUnitCode;
        this.abilityUnitName = abilityUnitName;
        this.abilityUnitDescription = abilityUnitDescription;
        this.lastSyncedAt = lastSyncedAt;
    }

    public void updateFromSync(String categoryPath, String jobName, String jobDescription,
                               String abilityUnitCode, String abilityUnitName,
                               String abilityUnitDescription, LocalDateTime lastSyncedAt) {
        this.categoryPath = categoryPath;
        this.jobName = jobName;
        this.jobDescription = jobDescription;
        this.abilityUnitCode = abilityUnitCode;
        this.abilityUnitName = abilityUnitName;
        this.abilityUnitDescription = abilityUnitDescription;
        this.lastSyncedAt = lastSyncedAt;
    }

    public Long getId() {
        return id;
    }

    public String getNcsCode() {
        return ncsCode;
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public String getJobName() {
        return jobName;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public String getAbilityUnitCode() {
        return abilityUnitCode;
    }

    public String getAbilityUnitName() {
        return abilityUnitName;
    }

    public String getAbilityUnitDescription() {
        return abilityUnitDescription;
    }

    public LocalDateTime getLastSyncedAt() {
        return lastSyncedAt;
    }
}