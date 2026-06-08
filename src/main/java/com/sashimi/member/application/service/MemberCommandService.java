package com.sashimi.member.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.member.application.command.ApplyInstructorCommand;
import com.sashimi.member.application.port.OcrPort;
import com.sashimi.member.application.usecase.MemberCommandUseCase;
import com.sashimi.member.domain.model.ApprovalStatus;
import com.sashimi.member.domain.model.InstructorApplication;
import com.sashimi.member.domain.model.InstructorCertification;
import com.sashimi.member.domain.repository.InstructorApplicationRepository;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandService implements MemberCommandUseCase {

    private final InstructorApplicationRepository instructorApplicationRepository;
    private final UserRepository userRepository;
    private final OcrPort ocrPort;

    @Override
    public void applyInstructor(ApplyInstructorCommand command) {

        if (command.bio() == null || command.bio().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        if (command.portfolioUrl() == null || command.portfolioUrl().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        // OCR 검증 - 성공한 파일들 모두 수집
        List<InstructorCertification> certifications = command.files().stream()
                .map(f -> ocrPort.extractCertificateInfo(f.fileBytes(), f.fileName()))
                .filter(OcrPort.OcrResult::success)
                .map(r -> InstructorCertification.of(r.certificationName(), r.issuedBy()))
                .collect(Collectors.toList());

        if (certifications.isEmpty()) {
            throw new BusinessException(ErrorCode.CERTIFICATE_OCR_FAILED);
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
                command.portfolioUrl(),
                certifications
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

        User user = userRepository.findById(application.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.promoteToInstructor();
        userRepository.save(user);
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