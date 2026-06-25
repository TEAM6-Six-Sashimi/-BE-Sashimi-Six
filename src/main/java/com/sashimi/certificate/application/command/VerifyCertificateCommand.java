package com.sashimi.certificate.application.command;

public record VerifyCertificateCommand(
        Long certificationId,
        Long userId,
        String userName,
        String identity,
        String phoneNo
) {}
