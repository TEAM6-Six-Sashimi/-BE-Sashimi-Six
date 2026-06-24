package com.sashimi.resume.infrastructure.persistence;

import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.model.ResumeCareer;
import com.sashimi.resume.domain.model.ResumeEducation;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resumes")
public class ResumeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_id")
    private Long resumeId;

    @Column(
            name = "user_id",
            nullable = false
    )
    private Long userId;

    @Column(
            name = "entry_level",
            nullable = false
    )
    private boolean entryLevel;

    @Column(
            name = "is_default",
            nullable = false
    )
    private boolean defaultResume;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "resume",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("educationOrder ASC")
    private  List<ResumeEducationJpaEntity> educations = new ArrayList<>();

    @OneToMany(
            mappedBy = "resume",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("careerOrder ASC")
    private  List<ResumeCareerJpaEntity> careers = new ArrayList<>();

    protected ResumeJpaEntity() {
    }

    public static ResumeJpaEntity from(
            Resume resume
    ) {
        ResumeJpaEntity entity =
                new ResumeJpaEntity();

        entity.resumeId = resume.resumeId();
        entity.userId = resume.userId();
        entity.entryLevel = resume.entryLevel();
        entity.defaultResume =
                resume.defaultResume();
        entity.createdAt = resume.createdAt();
        entity.updatedAt = resume.updatedAt();

        entity.replaceEducations(
                resume.educations()
        );

        entity.replaceCareers(
                resume.careers()
        );

        return entity;
    }

    public void updateFrom(Resume resume) {
        if (resume.resumeId() == null
                || !resume.resumeId()
                .equals(this.resumeId)) {
            throw new IllegalArgumentException(
                    "수정할 이력서 ID가 일치하지 않습니다."
            );
        }

        this.entryLevel = resume.entryLevel();
        this.defaultResume =
                resume.defaultResume();
        this.updatedAt = resume.updatedAt();

        replaceEducations(
                resume.educations()
        );

        replaceCareers(
                resume.careers()
        );
    }

    private void replaceEducations(
            List<ResumeEducation> newEducations
    ) {
        educations.clear();

        for (int index = 0;
             index < newEducations.size();
             index++) {
            educations.add(
                    ResumeEducationJpaEntity.from(
                            this,
                            newEducations.get(index),
                            index
                    )
            );
        }
    }

    private void replaceCareers(
            List<ResumeCareer> newCareers
    ) {
        careers.clear();

        for (int index = 0;
             index < newCareers.size();
             index++) {
            careers.add(
                    ResumeCareerJpaEntity.from(
                            this,
                            newCareers.get(index),
                            index
                    )
            );
        }
    }

    public Resume toDomain() {
        List<ResumeEducation> domainEducations =
                educations.stream()
                        .map(
                                ResumeEducationJpaEntity
                                        ::toDomain
                        )
                        .toList();

        List<ResumeCareer> domainCareers =
                careers.stream()
                        .map(
                                ResumeCareerJpaEntity
                                        ::toDomain
                        )
                        .toList();

        return Resume.restore(
                resumeId,
                userId,
                domainEducations,
                entryLevel,
                domainCareers,
                defaultResume,
                createdAt,
                updatedAt
        );
    }

    public Long getResumeId() {
        return resumeId;
    }

    public Long getUserId() {
        return userId;
    }
}