package com.sashimi.resume.presentation.api.request;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.domain.model.ResumeCareer;
import com.sashimi.resume.domain.model.ResumeCertification;
import com.sashimi.resume.domain.model.ResumeEducation;

import java.util.List;
import java.util.Objects;

public record CreateResumeRequest(
        List<ResumeEducationRequest> educations,
        boolean entryLevel,
        List<ResumeCareerRequest> careers,
        List<ResumeCertificationRequest> certifications,
        Boolean defaultResume
) {

    public List<ResumeEducation> toEducations() {
        if (educations == null) {
            return List.of();
        }

        if (educations.stream().anyMatch(Objects::isNull)) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT_VALUE
            );
        }

        return educations.stream()
                .map(ResumeEducationRequest::toDomain)
                .toList();
    }

    public List<ResumeCareer> toCareers() {
        if (careers == null) {
            return List.of();
        }

        if (careers.stream().anyMatch(Objects::isNull)) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT_VALUE
            );
        }

        return careers.stream()
                .map(ResumeCareerRequest::toDomain)
                .toList();
    }

    public List<ResumeCertification> toCertifications() {
        if (certifications == null) {
            return List.of();
        }

        if (certifications.stream().anyMatch(Objects::isNull)) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT_VALUE
            );
        }

        return certifications.stream()
                .map(ResumeCertificationRequest::toDomain)
                .toList();
    }
}