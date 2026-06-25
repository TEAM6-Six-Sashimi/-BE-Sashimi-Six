package com.sashimi.resume.application.command;

import com.sashimi.resume.domain.model.ResumeCareer;
import com.sashimi.resume.domain.model.ResumeCertification;
import com.sashimi.resume.domain.model.ResumeEducation;

import java.util.List;

public record UpdateResumeCommand(
        Long userId,
        Long resumeId,
        List<ResumeEducation> educations,
        Boolean entryLevel,
        List<ResumeCareer> careers,
        List<ResumeCertification> certifications,
        Boolean defaultResume
) {
}