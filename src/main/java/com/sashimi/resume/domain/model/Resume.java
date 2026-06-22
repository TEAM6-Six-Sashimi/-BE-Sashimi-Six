package com.sashimi.resume.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Resume {

    private final Long resumeId;
    private final Long userId;
    private final List<ResumeEducation> educations;
    private final boolean entryLevel;
    private final List<ResumeCareer> careers;
    private final boolean defaultResume;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Resume(
            Long resumeId,
            Long userId,
            List<ResumeEducation> educations,
            boolean entryLevel,
            List<ResumeCareer> careers,
            boolean defaultResume,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        validate(userId, educations, entryLevel, careers);

        this.resumeId = resumeId;
        this.userId = userId;
        this.educations = List.copyOf(educations);
        this.entryLevel = entryLevel;
        this.careers = List.copyOf(careers);
        this.defaultResume = defaultResume;
        this.createdAt = createdAt == null
                ? LocalDateTime.now()
                : createdAt;
        this.updatedAt = updatedAt;
    }

    public static Resume create(
            Long userId,
            List<ResumeEducation> educations,
            boolean entryLevel,
            List<ResumeCareer> careers,
            boolean defaultResume
    ) {
        return new Resume(
                null,
                userId,
                educations,
                entryLevel,
                careers,
                defaultResume,
                LocalDateTime.now(),
                null
        );
    }

    public Resume update(
            List<ResumeEducation> educations,
            Boolean entryLevel,
            List<ResumeCareer> careers,
            Boolean defaultResume
    ) {
        List<ResumeEducation> updatedEducations =
                educations == null ? this.educations : educations;

        boolean updatedEntryLevel =
                entryLevel == null ? this.entryLevel : entryLevel;

        List<ResumeCareer> updatedCareers =
                careers == null ? this.careers : careers;

        boolean updatedDefaultResume =
                defaultResume == null ? this.defaultResume : defaultResume;

        return new Resume(
                this.resumeId,
                this.userId,
                updatedEducations,
                updatedEntryLevel,
                updatedCareers,
                updatedDefaultResume,
                this.createdAt,
                LocalDateTime.now()
        );
    }

    public Resume withId(Long resumeId) {
        return new Resume(
                resumeId,
                this.userId,
                this.educations,
                this.entryLevel,
                this.careers,
                this.defaultResume,
                this.createdAt,
                this.updatedAt
        );
    }
    private static void validate(
            Long userId,
            List<ResumeEducation> educations,
            boolean entryLevel,
            List<ResumeCareer> careers
    ) {
        if (educations == null
                || educations.isEmpty()
                || educations.stream().anyMatch(Objects::isNull)) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT_VALUE
            );
        }

        if (careers == null
                || careers.stream().anyMatch(Objects::isNull)) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT_VALUE
            );
        }
    }

    public Long resumeId() {
        return resumeId;
    }

    public Long userId() {
        return userId;
    }

    public List<ResumeEducation> educations() {
        return educations;
    }

    public boolean entryLevel() {
        return entryLevel;
    }

    public List<ResumeCareer> careers() {
        return careers;
    }

    public boolean defaultResume() {
        return defaultResume;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public LocalDateTime updatedAt() {
        return updatedAt;
    }
}