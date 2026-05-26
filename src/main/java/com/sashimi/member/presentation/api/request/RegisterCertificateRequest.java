package com.sashimi.member.presentation.api.request;

import java.time.LocalDate;

public record RegisterCertificateRequest(
        String certificationName,
        String issuedBy,
        LocalDate issuedDate,
        String fileUrl
) {
}