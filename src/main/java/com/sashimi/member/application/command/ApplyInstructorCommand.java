package com.sashimi.member.application.command;

public record ApplyInstructorCommand(
        Long userId,
        String bio,
        String career,
        String portfolioUrl
) {
}