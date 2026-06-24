package com.sashimi.certificate.application.command;

public record DeleteCertificateCommand(
        Long userId,
        Long certificationId
) {
}
