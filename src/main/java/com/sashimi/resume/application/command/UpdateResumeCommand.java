package com.sashimi.resume.application.command;

import com.sashimi.resume.domain.model.ResumeTemplateType;

public record UpdateResumeCommand(
        Long userId,
        Long resumeId,
        String title,
        ResumeTemplateType templateType,
        String content,
        Boolean defaultResume
) {
}
