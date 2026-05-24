package com.sashimi.resume.application.command;

public record DeleteResumeCommand(
        Long userId,
        Long resumeId
) {
}
