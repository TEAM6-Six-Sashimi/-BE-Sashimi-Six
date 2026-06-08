package com.sashimi.resume.application.command;


public record UpdateResumeCommand(
        Long userId,
        Long resumeId,
        String title,
        String content,
        Boolean defaultResume
) {
}
