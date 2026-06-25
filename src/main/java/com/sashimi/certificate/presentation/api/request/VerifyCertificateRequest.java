package com.sashimi.certificate.presentation.api.request;

public record VerifyCertificateRequest(
        String userName,
        String identity,
        String phoneNo
) {}
