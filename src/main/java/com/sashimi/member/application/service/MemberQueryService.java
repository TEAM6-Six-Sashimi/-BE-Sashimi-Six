package com.sashimi.member.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.member.application.usecase.MemberQueryUseCase;
import com.sashimi.member.domain.model.ApprovalStatus;
import com.sashimi.member.domain.model.InstructorApplication;
import com.sashimi.member.domain.repository.InstructorApplicationRepository;
import com.sashimi.member.presentation.api.response.InstructorApplicationDetailResponse;
import com.sashimi.member.presentation.api.response.InstructorApplicationListResponse;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService implements MemberQueryUseCase {

    private final InstructorApplicationRepository instructorApplicationRepository;
    private final UserRepository userRepository;

    @Override
    public List<InstructorApplicationListResponse> getPendingInstructorApplications() {
        return instructorApplicationRepository.findAllByStatus(ApprovalStatus.PENDING)
                .stream()
                .map(application -> {
                    User user = userRepository.findById(application.getUserId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                    return InstructorApplicationListResponse.of(application, user);
                })
                .toList();
    }

    @Override
    public InstructorApplicationDetailResponse getInstructorApplicationDetail(Long applicationId) {
        InstructorApplication application = instructorApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));
        return InstructorApplicationDetailResponse.from(application);
    }
}
