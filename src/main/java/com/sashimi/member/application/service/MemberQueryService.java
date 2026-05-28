package com.sashimi.member.application.service;

import com.sashimi.member.application.usecase.MemberQueryUseCase;
import com.sashimi.member.domain.model.ApprovalStatus;
import com.sashimi.member.domain.repository.InstructorApplicationRepository;
import com.sashimi.member.presentation.api.response.InstructorApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService implements MemberQueryUseCase {

    private final InstructorApplicationRepository instructorApplicationRepository;

    @Override
    public List<InstructorApplicationResponse> getPendingInstructorApplications() {
        return instructorApplicationRepository.findAllByStatus(ApprovalStatus.PENDING)
                .stream()
                .map(InstructorApplicationResponse::from)
                .toList();
    }
}