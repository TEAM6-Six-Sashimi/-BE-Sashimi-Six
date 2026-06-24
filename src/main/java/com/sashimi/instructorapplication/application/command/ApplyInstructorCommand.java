package com.sashimi.instructorapplication.application.command;

import java.util.List;

public record ApplyInstructorCommand(
        Long userId,
        String bio,
        String motivationLetter,
        Long categoryId,
        String portfolioUrl,
        FileEntry profileImage,
        List<FileEntry> certificateFiles,
        FileEntry resumeFile
) {
    public record FileEntry(byte[] fileBytes, String fileName) {}
}
