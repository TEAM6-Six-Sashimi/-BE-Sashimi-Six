package com.sashimi.resume.application.command;

import com.sashimi.resume.domain.model.ResumeReviewSection;

public record CreateResumeCommand(
        Long userId,
        String title,
        String content,
        boolean defaultResume
) {
}
