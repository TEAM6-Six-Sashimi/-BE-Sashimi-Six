package com.sashimi.course.presentation.api.request;

import com.sashimi.course.domain.model.CourseDifficulty;
import com.sashimi.course.domain.model.CourseStatus;

import java.math.BigDecimal;
import java.util.List;

public record UpdateCourseRequest(
        Long categoryId,
        String title,
        String description,
        BigDecimal price,
        CourseDifficulty difficulty,
        String thumbnail,
        CourseStatus targetStatus,
        List<CreateSessionRequest> sessions
) {}
