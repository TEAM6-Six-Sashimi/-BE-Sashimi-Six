package com.sashimi.member.application.command;

public record DeleteCertificateCommand(
        Long userId,
        Long certificationId
) {
}