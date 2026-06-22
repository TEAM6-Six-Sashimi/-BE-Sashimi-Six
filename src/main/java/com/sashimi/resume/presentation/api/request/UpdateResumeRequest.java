package com.sashimi.resume.presentation.api.request;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.domain.model.ResumeCareer;
import com.sashimi.resume.domain.model.ResumeEducation;

import java.util.List;
import java.util.Objects;

public record UpdateResumeRequest(
        List<ResumeEducationRequest> educations,
        Boolean entryLevel,
        List<ResumeCareerRequest> careers,
        Boolean defaultResume
) {

    public List<ResumeEducation> toEducations() {
        if (educations == null) {
            return null;
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
            return null;
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
}