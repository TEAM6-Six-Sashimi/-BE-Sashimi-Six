package com.sashimi.member.application.usecase;

import com.sashimi.member.presentation.api.response.InstructorApplicationDetailResponse;
import com.sashimi.member.presentation.api.response.InstructorApplicationListResponse;
import com.sashimi.member.presentation.api.response.MyInstructorApplicationDetailResponse;
import com.sashimi.member.presentation.api.response.MyInstructorApplicationListResponse;
import com.sashimi.member.presentation.api.response.RejectedApplicationListResponse;

import java.util.List;

public interface MemberQueryUseCase {

    List<InstructorApplicationListResponse> getPendingInstructorApplications();

    InstructorApplicationDetailResponse getInstructorApplicationDetail(Long applicationId);

    List<MyInstructorApplicationListResponse> getMyInstructorApplications(Long userId);

    MyInstructorApplicationDetailResponse getMyInstructorApplicationDetail(Long userId, Long applicationId);

    List<RejectedApplicationListResponse> getRejectedInstructorApplications();
}