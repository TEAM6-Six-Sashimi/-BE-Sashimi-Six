package com.sashimi.member.application.usecase;

import com.sashimi.member.presentation.api.response.CertificateResponse;

import java.util.List;

public interface CertificateQueryUseCase {

    List<CertificateResponse> getCertificates(Long userId);
}