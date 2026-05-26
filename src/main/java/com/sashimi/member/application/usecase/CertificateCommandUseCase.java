package com.sashimi.member.application.usecase;

import com.sashimi.member.application.command.DeleteCertificateCommand;
import com.sashimi.member.application.command.RegisterCertificateCommand;

public interface CertificateCommandUseCase {

    void registerCertificate(RegisterCertificateCommand command);

    void deleteCertificate(DeleteCertificateCommand command);
}