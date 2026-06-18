package com.sashimi.course.application.command;

import com.sashimi.course.domain.model.CourseDifficulty;
import com.sashimi.course.domain.model.CourseStatus;

import java.util.List;

public record UpdateCourseCommand(
        Long courseId,
        Long instructorId,
        Long categoryId,
        String title,
        String description,
        Long price,
        CourseDifficulty difficulty,
        String thumbnail,
        Long ncsInfoId,
        CourseStatus targetStatus,
        List<CreateSessionCommand> sessions
) {}
