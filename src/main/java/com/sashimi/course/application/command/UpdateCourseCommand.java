package com.sashimi.course.application.command;

import com.sashimi.course.domain.model.CourseDifficulty;
import com.sashimi.course.domain.model.CourseStatus;

import java.math.BigDecimal;
import java.util.List;

public record UpdateCourseCommand(
        Long courseId,
        Long instructorId,
        Long categoryId,
        String title,
        String description,
        BigDecimal price,
        CourseDifficulty difficulty,
        String thumbnail,
        CourseStatus targetStatus,
        List<CreateSessionCommand> sessions
) {}
