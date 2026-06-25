package com.sashimi.certificate.application.usecase;

import com.sashimi.certificate.application.command.DeleteCertificateCommand;
import com.sashimi.certificate.application.command.RegisterCertificateCommand;
import com.sashimi.certificate.application.command.VerifyCertificateCommand;
import com.sashimi.certificate.presentation.api.response.CertificateResponse;

import java.util.List;

public interface CertificateCommandUseCase {

    List<CertificateResponse> registerCertificates(RegisterCertificateCommand command);

    void deleteCertificate(DeleteCertificateCommand command);

    void verifyCertificate(VerifyCertificateCommand command);
}
