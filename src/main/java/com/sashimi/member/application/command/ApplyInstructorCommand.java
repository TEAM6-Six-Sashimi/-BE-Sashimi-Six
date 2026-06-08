package com.sashimi.member.application.command;

import java.util.List;

public record ApplyInstructorCommand(
        Long userId,
        String bio,
        String portfolioUrl,
        List<FileEntry> files
) {
    public record FileEntry(byte[] fileBytes, String fileName) {}
}