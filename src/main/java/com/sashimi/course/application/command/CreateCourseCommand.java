package com.sashimi.course.application.command;

import com.sashimi.course.domain.model.CourseDifficulty;
import com.sashimi.course.domain.model.CourseStatus;

import java.math.BigDecimal;
import java.util.List;

public record CreateCourseCommand(
        Long instructorId,
        String subCategoryName,
        String title,
        String description,
        BigDecimal price,
        CourseDifficulty difficulty,
        String thumbnail,
        CourseStatus initialStatus,
        List<CreateSessionCommand> sessions
) {}
