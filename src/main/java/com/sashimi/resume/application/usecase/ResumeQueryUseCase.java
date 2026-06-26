package com.sashimi.resume.application.usecase;

import com.sashimi.resume.domain.model.Resume;

public interface ResumeQueryUseCase {

    Resume getMyResume(Long userId);
}