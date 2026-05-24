package com.sashimi.resume.application.command;

public record ReviewResumeCommand(
        Long userId,
        Long resumeId,
        Long jobPostingId
) {
}
