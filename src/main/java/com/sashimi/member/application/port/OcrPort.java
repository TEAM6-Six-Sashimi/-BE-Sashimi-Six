package com.sashimi.member.application.port;

import java.time.LocalDate;

public interface OcrPort {

    OcrResult extractCertificateInfo(String fileUrl);

    record OcrResult(
            String certificationName,
            String issuedBy,
            LocalDate issuedDate,
            boolean success
    ) {}
}