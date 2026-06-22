package com.sashimi.resume.application.command;

import com.sashimi.resume.domain.model.ResumeCareer;
import com.sashimi.resume.domain.model.ResumeEducation;

import java.util.List;

public record CreateResumeCommand(
        Long userId,
        List<ResumeEducation> educations,
        boolean entryLevel,
        List<ResumeCareer> careers,
        boolean defaultResume
) {
}