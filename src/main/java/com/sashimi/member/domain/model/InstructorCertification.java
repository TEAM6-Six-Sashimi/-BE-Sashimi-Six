package com.sashimi.member.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InstructorCertification {

    private Long id;
    private String certificationName;
    private String issuedBy;
    private String filePath;

    public static InstructorCertification of(String certificationName, String issuedBy, String filePath) {
        return InstructorCertification.builder()
                .certificationName(certificationName)
                .issuedBy(issuedBy)
                .filePath(filePath)
                .build();
    }
}
