package com.sashimi.resume.presentation.api.response;

import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.model.ResumeTemplateType;

import java.time.LocalDateTime;

public record ResumeResponse(
        Long resumeId,
        String title,
        ResumeTemplateType templateType,
        String content,
        boolean defaultResume,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ResumeResponse from(Resume resume) {
        return new ResumeResponse(
                resume.resumeId(),
                resume.title(),
                resume.templateType(),
                resume.content(),
                resume.defaultResume(),
                resume.createdAt(),
                resume.updatedAt()
        );
    }
}
