package com.sashimi.instructorapplication.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InstructorCertification {

    private Long id;
    private String certificationName;
    private String issuedBy;
    private String filePath;
    private String certificationNumber;
    private VerificationStatus verificationStatus;

    public static InstructorCertification of(
            String certificationName, String issuedBy, String filePath, String certificationNumber) {
        return InstructorCertification.builder()
                .certificationName(certificationName)
                .issuedBy(issuedBy)
                .filePath(filePath)
                .certificationNumber(certificationNumber)
                .verificationStatus(VerificationStatus.PENDING)
                .build();
    }
}
