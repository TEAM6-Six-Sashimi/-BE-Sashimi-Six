package com.sashimi.resume.application.usecase;

import com.sashimi.resume.domain.model.Resume;

import java.util.List;

public interface ResumeQueryUseCase {

    List<Resume> getMyResumes(Long userId);
}
