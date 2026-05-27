package com.sashimi.member.application.command;

import java.time.LocalDate;

public record RegisterCertificateCommand(
        Long userId,
        String certificationName,
        String issuedBy,
        LocalDate issuedDate,
        String fileUrl
) {
}