package com.sashimi.resume.presentation.api.request;

import com.sashimi.resume.domain.model.ResumeCareer;
import com.sashimi.resume.domain.model.ResumeEducation;

import java.util.List;

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

        return educations.stream()
                .map(ResumeEducationRequest::toDomain)
                .toList();
    }

    public List<ResumeCareer> toCareers() {
        if (careers == null) {
            return null;
        }

        return careers.stream()
                .map(ResumeCareerRequest::toDomain)
                .toList();
    }
}