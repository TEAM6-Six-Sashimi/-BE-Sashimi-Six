package com.sashimi.certificate.application.command;

import java.util.List;

public record RegisterCertificateCommand(
        Long userId,
        List<FileEntry> files
) {
    public record FileEntry(byte[] fileBytes, String fileName) {}
}
