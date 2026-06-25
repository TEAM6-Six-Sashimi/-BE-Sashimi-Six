package com.sashimi.certificate.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.certificate.application.command.RegisterCertificateCommand;
import com.sashimi.certificate.application.port.CertificationVerificationPort;
import com.sashimi.certificate.application.port.OcrPort;
import com.sashimi.certificate.domain.model.UserCertification;
import com.sashimi.certificate.domain.repository.UserCertificationRepository;
import com.sashimi.certificate.presentation.api.response.CertificateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CertificateCommandServiceTest {

    private UserCertificationRepository userCertificationRepository;
    private OcrPort ocrPort;
    private CertificationVerificationPort certificationVerificationPort;
    private CertificateCommandService certificateCommandService;

    @BeforeEach
    void setUp() {
        userCertificationRepository = mock(UserCertificationRepository.class);
        ocrPort = mock(OcrPort.class);
        certificationVerificationPort = mock(CertificationVerificationPort.class);
        certificateCommandService = new CertificateCommandService(
                userCertificationRepository,
                ocrPort,
                certificationVerificationPort
        );
    }

    @Test
    void OCR_성공시_자격증이_PENDING으로_저장된다() {
        // given
        byte[] fileBytes = "test".getBytes();
        String fileName = "자격증.pdf";
        RegisterCertificateCommand command = new RegisterCertificateCommand(
                1L,
                List.of(new RegisterCertificateCommand.FileEntry(fileBytes, fileName))
        );

        OcrPort.OcrResult ocrResult = new OcrPort.OcrResult(
                "정보처리기사",
                "한국산업인력공단",
                LocalDate.of(2024, 1, 15),
                true
        );

        when(ocrPort.extractCertificateInfo(fileBytes, fileName)).thenReturn(ocrResult);
        when(userCertificationRepository.save(any(UserCertification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        List<CertificateResponse> results = certificateCommandService.registerCertificates(command);

        // then
        assertThat(results).hasSize(1);
        verify(ocrPort).extractCertificateInfo(fileBytes, fileName);
        verify(userCertificationRepository).save(any(UserCertification.class));
    }

    @Test
    void OCR_실패시_예외가_발생한다() {
        // given
        byte[] fileBytes = "test".getBytes();
        String fileName = "자격증.pdf";
        RegisterCertificateCommand command = new RegisterCertificateCommand(
                1L,
                List.of(new RegisterCertificateCommand.FileEntry(fileBytes, fileName))
        );

        OcrPort.OcrResult ocrResult = new OcrPort.OcrResult(null, null, null, false);

        when(ocrPort.extractCertificateInfo(fileBytes, fileName)).thenReturn(ocrResult);

        // when & then
        assertThatThrownBy(() ->
                certificateCommandService.registerCertificates(command)
        ).isInstanceOf(BusinessException.class);

        verify(ocrPort).extractCertificateInfo(fileBytes, fileName);
        verifyNoInteractions(userCertificationRepository);
    }
}
