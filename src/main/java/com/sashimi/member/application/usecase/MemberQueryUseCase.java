package com.sashimi.member.application.usecase;

import com.sashimi.member.presentation.api.response.InstructorApplicationResponse;

import java.util.List;

public interface MemberQueryUseCase {

    List<InstructorApplicationResponse> getPendingInstructorApplications();
}