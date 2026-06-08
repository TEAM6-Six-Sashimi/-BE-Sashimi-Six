package com.sashimi.course.application.usecase;

import com.sashimi.course.application.command.ApproveCourseCommand;
import com.sashimi.course.application.command.CreateCourseCommand;
import com.sashimi.course.application.command.DeleteCourseCommand;
import com.sashimi.course.application.command.RejectCourseCommand;
import com.sashimi.course.application.command.UpdateCourseCommand;

public interface CourseCommandUseCase {
    Long createCourse(CreateCourseCommand command);
    void updateCourse(UpdateCourseCommand command);
    void deleteCourse(DeleteCourseCommand command);
    void approveCourse(ApproveCourseCommand command);
    void rejectCourse(RejectCourseCommand command);
}
