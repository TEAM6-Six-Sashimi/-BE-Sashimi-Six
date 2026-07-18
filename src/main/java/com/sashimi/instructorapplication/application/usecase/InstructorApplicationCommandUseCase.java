package com.sashimi.instructorapplication.application.usecase;

import com.sashimi.instructorapplication.application.command.ApplyInstructorCommand;
import com.sashimi.instructorapplication.domain.model.RejectionCategory;

public interface InstructorApplicationCommandUseCase {

    void applyInstructor(ApplyInstructorCommand command);

    void approveInstructor(Long applicationId);

    void rejectInstructor(Long applicationId, RejectionCategory rejectionCategory, String rejectionReason);

    byte[] generateVerificationExcel();
}
