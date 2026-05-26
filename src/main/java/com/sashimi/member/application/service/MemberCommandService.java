package com.sashimi.member.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.member.application.command.ApplyInstructorCommand;
import com.sashimi.member.application.usecase.MemberCommandUseCase;
import com.sashimi.member.domain.model.ApprovalStatus;
import com.sashimi.member.domain.model.InstructorApplication;
import com.sashimi.member.domain.repository.InstructorApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandService implements MemberCommandUseCase {

    private final InstructorApplicationRepository instructorApplicationRepository;

    @Override
    public void applyInstructor(ApplyInstructorCommand command) {

        // 필수값 검증
        if (command.bio() == null || command.bio().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        if (command.career() == null || command.career().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        if (command.portfolioUrl() == null || command.portfolioUrl().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        // 중복 신청 방지
        boolean alreadyApplied = instructorApplicationRepository
                .existsByUserIdAndApprovalStatus(command.userId(), ApprovalStatus.PENDING);

        if (alreadyApplied) {
            throw new BusinessException(ErrorCode.ALREADY_APPLIED);
        }

        InstructorApplication application = InstructorApplication.create(
                command.userId(),
                command.bio(),
                command.career(),
                command.portfolioUrl()
        );

        instructorApplicationRepository.save(application);
    }

    @Override
    public void approveInstructor(Long applicationId) {
        InstructorApplication application = instructorApplicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        application.approve();
        instructorApplicationRepository.save(application);
    }

    @Override
    public void rejectInstructor(Long applicationId) {
        InstructorApplication application = instructorApplicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        application.reject();
        instructorApplicationRepository.save(application);
    }
}