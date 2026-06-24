package com.sashimi.certificate.application.usecase;

import com.sashimi.certificate.presentation.api.response.CertificateResponse;

import java.util.List;

public interface CertificateQueryUseCase {

    List<CertificateResponse> getCertificates(Long userId);
}
