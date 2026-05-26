package com.sashimi.member.application.usecase;

import com.sashimi.member.application.command.ApplyInstructorCommand;

public interface MemberCommandUseCase {

    void applyInstructor(ApplyInstructorCommand command);

    void approveInstructor(Long applicationId);

    void rejectInstructor(Long applicationId);
}