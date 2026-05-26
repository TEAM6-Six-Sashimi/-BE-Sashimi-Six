package com.sashimi.member.application.service;

import com.sashimi.member.application.command.DeleteCertificateCommand;
import com.sashimi.member.application.command.RegisterCertificateCommand;
import com.sashimi.member.application.port.OcrPort;
import com.sashimi.member.application.usecase.CertificateCommandUseCase;
import com.sashimi.member.domain.model.UserCertification;
import com.sashimi.member.domain.repository.UserCertificationRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificateCommandService implements CertificateCommandUseCase {

    private final UserCertificationRepository userCertificationRepository;
    private final OcrPort ocrPort;

    @Override
    public void registerCertificate(RegisterCertificateCommand command) {

        // OCR 검증
        OcrPort.OcrResult result = ocrPort.extractCertificateInfo(command.fileUrl());

        if (!result.success()) {
            // OCR 실패 → REJECTED로 저장
            UserCertification rejected = UserCertification.create(
                    command.userId(),
                    command.certificationName(),
                    command.issuedBy(),
                    command.issuedDate(),
                    command.fileUrl()
            );
            rejected.reject();
            userCertificationRepository.save(rejected);
            throw new BusinessException(ErrorCode.CERTIFICATE_OCR_FAILED);
        }

        // OCR 성공 → VERIFIED로 저장
        UserCertification certification = UserCertification.create(
                command.userId(),
                result.certificationName(),
                result.issuedBy(),
                result.issuedDate(),
                command.fileUrl()
        );
        certification.verify();
        userCertificationRepository.save(certification);
    }

    @Override
    public void deleteCertificate(DeleteCertificateCommand command) {
        UserCertification certification = userCertificationRepository
                .findById(command.certificationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CERTIFICATE_NOT_FOUND));

        // 본인 자격증인지 확인
        if (!certification.getUserId().equals(command.userId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        certification.delete();
        userCertificationRepository.save(certification);
    }
}