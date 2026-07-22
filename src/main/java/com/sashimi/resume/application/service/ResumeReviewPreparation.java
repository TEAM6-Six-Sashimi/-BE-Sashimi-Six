package com.sashimi.resume.application.service;

import com.sashimi.resume.domain.model.Resume;

public record ResumeReviewPreparation(
        Long historyId,
        Resume resume
) {
}