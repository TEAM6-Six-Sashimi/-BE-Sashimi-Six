package com.sashimi.certificate.application.port;

import java.time.LocalDate;

public interface OcrPort {

    OcrResult extractCertificateInfo(byte[] fileBytes, String fileName);

    record OcrResult(
            String certificationName,
            String issuedBy,
            LocalDate issuedDate,
            boolean success,
            String certificationNumber
    ) {}
}
