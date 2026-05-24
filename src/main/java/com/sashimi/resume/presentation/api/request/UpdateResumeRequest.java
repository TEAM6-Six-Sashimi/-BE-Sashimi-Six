package com.sashimi.resume.presentation.api.request;

import com.sashimi.resume.domain.model.ResumeTemplateType;

public record UpdateResumeRequest(
        String title,
        ResumeTemplateType templateType,
        String content,
        Boolean defaultResume
) {
}
