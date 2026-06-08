package com.sashimi.resume.application.usecase;

import com.sashimi.resume.application.command.CreateResumeCommand;
import com.sashimi.resume.application.command.DeleteResumeCommand;
import com.sashimi.resume.application.command.UpdateResumeCommand;
import com.sashimi.resume.domain.model.Resume;

public interface ResumeCommandUseCase {

    Resume create(CreateResumeCommand command);

    Resume update(UpdateResumeCommand command);

    void delete(DeleteResumeCommand command);
}
