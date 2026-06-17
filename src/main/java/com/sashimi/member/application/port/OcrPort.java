package com.sashimi.member.application.port;

import java.time.LocalDate;
import java.util.List;

public interface OcrPort {

    OcrResult extractCertificateInfo(byte[] fileBytes, String fileName);

    List<String> extractMainCareers(byte[] fileBytes, String fileName);

    record OcrResult(
            String certificationName,
            String issuedBy,
            LocalDate issuedDate,
            boolean success
    ) {}
}