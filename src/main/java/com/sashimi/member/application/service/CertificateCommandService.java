package com.sashimi.member.application.service;

import com.sashimi.member.application.command.DeleteCertificateCommand;
import com.sashimi.member.application.command.RegisterCertificateCommand;
import com.sashimi.member.application.port.OcrPort;
import com.sashimi.member.application.usecase.CertificateCommandUseCase;
import com.sashimi.member.domain.model.UserCertification;
import com.sashimi.member.domain.repository.UserCertificationRepository;
import com.sashimi.member.presentation.api.response.CertificateResponse;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificateCommandService implements CertificateCommandUseCase {

    private final UserCertificationRepository userCertificationRepository;
    private final OcrPort ocrPort;

    @Override
    public List<CertificateResponse> registerCertificates(RegisterCertificateCommand command) {
        return command.files().stream()
                .map(file -> registerSingle(command.userId(), file))
                .toList();
    }

    @Override
    public void deleteCertificate(DeleteCertificateCommand command) {
        UserCertification certification = userCertificationRepository
                .findById(command.certificationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CERTIFICATE_NOT_FOUND));

        if (!certification.getUserId().equals(command.userId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        certification.delete();
        userCertificationRepository.save(certification);
    }

    private CertificateResponse registerSingle(Long userId, RegisterCertificateCommand.FileEntry file) {
        OcrPort.OcrResult result = ocrPort.extractCertificateInfo(file.fileBytes(), file.fileName());

        if (!result.success()) {
            throw new BusinessException(ErrorCode.CERTIFICATE_OCR_FAILED);
        }

        UserCertification certification = UserCertification.create(
                userId,
                result.certificationName(),
                result.issuedBy(),
                result.issuedDate(),
                file.fileName()
        );
        certification.verify();
        return CertificateResponse.from(userCertificationRepository.save(certification));
    }
}
