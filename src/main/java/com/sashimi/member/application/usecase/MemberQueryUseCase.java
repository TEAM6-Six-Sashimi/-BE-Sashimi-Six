package com.sashimi.member.application.usecase;

import com.sashimi.member.presentation.api.response.InstructorApplicationDetailResponse;
import com.sashimi.member.presentation.api.response.InstructorApplicationListResponse;

import java.util.List;

public interface MemberQueryUseCase {

    List<InstructorApplicationListResponse> getPendingInstructorApplications();

    InstructorApplicationDetailResponse getInstructorApplicationDetail(Long applicationId);
}