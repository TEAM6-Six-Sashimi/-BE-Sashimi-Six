package com.sashimi.resume.presentation.api.request;

import com.sashimi.resume.domain.model.ResumeCareer;
import com.sashimi.resume.domain.model.ResumeEducation;

import java.util.List;

public record CreateResumeRequest(
        List<ResumeEducationRequest> educations,
        boolean entryLevel,
        List<ResumeCareerRequest> careers,
        Boolean defaultResume
) {

    public List<ResumeEducation> toEducations() {
        if (educations == null) {
            return List.of();
        }

        return educations.stream()
                .map(ResumeEducationRequest::toDomain)
                .toList();
    }

    public List<ResumeCareer> toCareers() {
        if (careers == null) {
            return List.of();
        }

        return careers.stream()
                .map(ResumeCareerRequest::toDomain)
                .toList();
    }
}