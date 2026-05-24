package com.sashimi.resume.application.command;

import com.sashimi.resume.domain.model.ResumeTemplateType;

public record CreateResumeCommand(
        Long userId,
        String title,
        ResumeTemplateType templateType,
        String content,
        boolean defaultResume
) {
}
