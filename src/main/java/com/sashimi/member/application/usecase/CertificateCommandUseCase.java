package com.sashimi.member.application.usecase;

import com.sashimi.member.application.command.DeleteCertificateCommand;
import com.sashimi.member.application.command.RegisterCertificateCommand;
import com.sashimi.member.presentation.api.response.CertificateResponse;

public interface CertificateCommandUseCase {

    CertificateResponse registerCertificate(RegisterCertificateCommand command);

    void deleteCertificate(DeleteCertificateCommand command);
}