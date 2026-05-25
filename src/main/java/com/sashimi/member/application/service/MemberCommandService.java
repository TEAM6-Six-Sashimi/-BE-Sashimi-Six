package com.sashimi.member.application.service;

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
            throw new IllegalArgumentException("자기소개는 필수입니다.");
        }
        if (command.career() == null || command.career().isBlank()) {
            throw new IllegalArgumentException("이력서는 필수입니다.");
        }
        if (command.portfolioUrl() == null || command.portfolioUrl().isBlank()) {
            throw new IllegalArgumentException("포트폴리오는 필수입니다.");
        }

        // 중복 신청 방지
        boolean alreadyApplied = instructorApplicationRepository
                .existsByUserIdAndApprovalStatus(command.userId(), ApprovalStatus.PENDING);

        if (alreadyApplied) {
            throw new IllegalStateException("이미 강사 신청이 진행 중입니다.");
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
                .orElseThrow(() -> new IllegalArgumentException("신청을 찾을 수 없습니다."));

        application.approve();
        instructorApplicationRepository.save(application);
    }

    @Override
    public void rejectInstructor(Long applicationId) {
        InstructorApplication application = instructorApplicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("신청을 찾을 수 없습니다."));

        application.reject();
        instructorApplicationRepository.save(application);
    }
}