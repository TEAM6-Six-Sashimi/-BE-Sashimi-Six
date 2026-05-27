package com.sashimi.member.presentation.api.request;

import org.springframework.web.multipart.MultipartFile;

public record RegisterCertificateRequest(
        MultipartFile file
) {
}