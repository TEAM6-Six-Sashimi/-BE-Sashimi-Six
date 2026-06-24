package com.sashimi.certificate.application.service;

import com.sashimi.certificate.application.usecase.CertificateQueryUseCase;
import com.sashimi.certificate.domain.repository.UserCertificationRepository;
import com.sashimi.certificate.presentation.api.response.CertificateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificateQueryService implements CertificateQueryUseCase {

    private final UserCertificationRepository userCertificationRepository;

    @Override
    public List<CertificateResponse> getCertificates(Long userId) {
        return userCertificationRepository.findAllByUserId(userId)
                .stream()
                .map(CertificateResponse::from)
                .toList();
    }
}
