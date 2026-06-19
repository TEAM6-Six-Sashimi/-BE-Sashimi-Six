package com.sashimi.member.application.usecase;

import com.sashimi.member.application.command.ApplyInstructorCommand;
import com.sashimi.member.domain.model.RejectionCategory;

public interface MemberCommandUseCase {

    void applyInstructor(ApplyInstructorCommand command);

    void approveInstructor(Long applicationId);

    void rejectInstructor(Long applicationId, RejectionCategory rejectionCategory, String rejectionReason);
}