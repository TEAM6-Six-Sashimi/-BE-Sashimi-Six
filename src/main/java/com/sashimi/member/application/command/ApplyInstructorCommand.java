package com.sashimi.member.application.command;

public record ApplyInstructorCommand(
        Long userId,
        String bio,
        String portfolioUrl,
        byte[] fileBytes,
        String fileName
) {
}