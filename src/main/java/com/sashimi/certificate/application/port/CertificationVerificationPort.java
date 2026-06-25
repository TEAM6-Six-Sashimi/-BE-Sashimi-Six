package com.sashimi.certificate.application.port;

public interface CertificationVerificationPort {

    VerificationResult verify(VerificationRequest request);

    record VerificationRequest(
            String userName,
            String identity,
            String phoneNo,
            String telecom,
            String loginTypeLevel
    ) {}

    record VerificationResult(
            String certNo,
            String category,
            String passDate,
            boolean success
    ) {}
}
