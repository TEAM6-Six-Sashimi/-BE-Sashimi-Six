package com.sashimi.resume.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.application.usecase.ResumeQueryUseCase;
import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.repository.ResumeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResumeQueryService implements ResumeQueryUseCase {

    private final ResumeRepository resumeRepository;

    public ResumeQueryService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resume> getMyResumes(Long userId) {
        return resumeRepository.findAllByUserId(userId);
    }
}
