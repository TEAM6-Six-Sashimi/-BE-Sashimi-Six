package com.sashimi.resume.presentation.api.response;

import com.sashimi.resume.domain.model.Resume;

import java.time.LocalDateTime;
import java.util.List;

public record ResumeResponse(
        Long resumeId,
        List<ResumeEducationResponse> educations,
        boolean entryLevel,
        List<ResumeCareerResponse> careers,
        boolean defaultResume,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ResumeResponse from(Resume resume) {
        List<ResumeEducationResponse> educations =
                resume.educations().stream()
                        .map(ResumeEducationResponse::from)
                        .toList();

        List<ResumeCareerResponse> careers =
                resume.careers().stream()
                        .map(ResumeCareerResponse::from)
                        .toList();

        return new ResumeResponse(
                resume.resumeId(),
                educations,
                resume.entryLevel(),
                careers,
                resume.defaultResume(),
                resume.createdAt(),
                resume.updatedAt()
        );
    }
}