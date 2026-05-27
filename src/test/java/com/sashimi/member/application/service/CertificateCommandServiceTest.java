package com.sashimi.member.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.member.application.command.RegisterCertificateCommand;
import com.sashimi.member.application.port.OcrPort;
import com.sashimi.member.domain.model.UserCertification;
import com.sashimi.member.domain.repository.UserCertificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CertificateCommandServiceTest {

    private UserCertificationRepository userCertificationRepository;
    private OcrPort ocrPort;
    private CertificateCommandService certificateCommandService;

    @BeforeEach
    void setUp() {
        userCertificationRepository = mock(UserCertificationRepository.class);
        ocrPort = mock(OcrPort.class);
        certificateCommandService = new CertificateCommandService(
                userCertificationRepository,
                ocrPort
        );
    }

    @Test
    void OCR_성공시_자격증이_VERIFIED로_저장된다() {
        // given
        byte[] fileBytes = "test".getBytes();
        String fileName = "자격증.pdf";
        RegisterCertificateCommand command = new RegisterCertificateCommand(1L, fileBytes, fileName);

        OcrPort.OcrResult ocrResult = new OcrPort.OcrResult(
                "정보처리기사",
                "한국산업인력공단",
                LocalDate.of(2024, 1, 15),
                true
        );

        when(ocrPort.extractCertificateInfo(fileBytes, fileName)).thenReturn(ocrResult);
        when(userCertificationRepository.save(any(UserCertification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when & then
        assertThatNoException().isThrownBy(() ->
                certificateCommandService.registerCertificate(command)
        );

        verify(ocrPort).extractCertificateInfo(fileBytes, fileName);
        verify(userCertificationRepository).save(any(UserCertification.class));
    }

    @Test
    void OCR_실패시_예외가_발생한다() {
        // given
        byte[] fileBytes = "test".getBytes();
        String fileName = "자격증.pdf";
        RegisterCertificateCommand command = new RegisterCertificateCommand(1L, fileBytes, fileName);

        OcrPort.OcrResult ocrResult = new OcrPort.OcrResult(null, null, null, false);

        when(ocrPort.extractCertificateInfo(fileBytes, fileName)).thenReturn(ocrResult);

        // when & then
        assertThatThrownBy(() ->
                certificateCommandService.registerCertificate(command)
        ).isInstanceOf(BusinessException.class);

        verify(ocrPort).extractCertificateInfo(fileBytes, fileName);
        verifyNoInteractions(userCertificationRepository);
    }
}