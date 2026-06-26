package com.sashimi.resume.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.application.command.CreateResumeCommand;
import com.sashimi.resume.application.command.DeleteResumeCommand;
import com.sashimi.resume.application.command.UpdateResumeCommand;
import com.sashimi.resume.application.usecase.ResumeCommandUseCase;
import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.repository.ResumeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class ResumeCommandService implements ResumeCommandUseCase {

    private final ResumeRepository resumeRepository;

    public ResumeCommandService(
            ResumeRepository resumeRepository
    ) {
        this.resumeRepository = resumeRepository;
    }

    @Override
    public Resume create(CreateResumeCommand command) {
        if (resumeRepository.existsByUserId(command.userId())) {
            throw new BusinessException(
                    ErrorCode.RESUME_ALREADY_EXISTS
            );
        }

        Resume resume = Resume.create(
                command.userId(),
                command.educations(),
                command.entryLevel(),
                command.careers(),
                command.certifications(),
                command.defaultResume()
        );

        Resume savedResume;

        try {
            savedResume = resumeRepository.save(resume);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(
                    ErrorCode.RESUME_ALREADY_EXISTS
            );
        }

        log.info(
                "이력서 생성: userId={}, resumeId={}, entryLevel={}, defaultResume={}",
                command.userId(),
                savedResume.resumeId(),
                savedResume.entryLevel(),
                savedResume.defaultResume()
        );

        return savedResume;
    }

    @Override
    public Resume update(UpdateResumeCommand command) {
        Resume resume = resumeRepository
                .findByIdAndUserId(
                        command.resumeId(),
                        command.userId()
                )
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.RESUME_NOT_FOUND
                        )
                );

        Resume updatedResume = resume.update(
                command.educations(),
                command.entryLevel(),
                command.careers(),
                command.certifications(),
                command.defaultResume()
        );

        Resume savedResume = resumeRepository.save(
                updatedResume
        );

        log.info(
                "이력서 수정: userId={}, resumeId={}, entryLevel={}, defaultResume={}",
                command.userId(),
                savedResume.resumeId(),
                savedResume.entryLevel(),
                savedResume.defaultResume()
        );

        return savedResume;
    }

    @Override
    public void delete(DeleteResumeCommand command) {
        Resume resume = resumeRepository
                .findByIdAndUserId(
                        command.resumeId(),
                        command.userId()
                )
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.RESUME_NOT_FOUND
                        )
                );

        resumeRepository.delete(resume);

        log.info(
                "이력서 삭제: userId={}, resumeId={}",
                command.userId(),
                command.resumeId()
        );
    }
}
