package com.sashimi.member.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InstructorCertification {

    private Long id;
    private String certificationName;
    private String issuedBy;

    public static InstructorCertification of(String certificationName, String issuedBy) {
        return InstructorCertification.builder()
                .certificationName(certificationName)
                .issuedBy(issuedBy)
                .build();
    }
}
