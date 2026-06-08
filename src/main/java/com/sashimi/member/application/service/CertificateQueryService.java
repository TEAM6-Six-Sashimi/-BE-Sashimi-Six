package com.sashimi.member.application.service;

import com.sashimi.member.application.usecase.CertificateQueryUseCase;
import com.sashimi.member.domain.repository.UserCertificationRepository;
import com.sashimi.member.presentation.api.response.CertificateResponse;
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