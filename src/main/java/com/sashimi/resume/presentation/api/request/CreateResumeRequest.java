package com.sashimi.resume.presentation.api.request;

import com.sashimi.resume.domain.model.ResumeTemplateType;

public record CreateResumeRequest(
        String title,
        ResumeTemplateType templateType,
        String content,
        Boolean defaultResume
) {
}
