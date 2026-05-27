package com.sashimi.member.application.command;

public record RegisterCertificateCommand(
        Long userId,
        byte[] fileBytes,
        String fileName
) {
}