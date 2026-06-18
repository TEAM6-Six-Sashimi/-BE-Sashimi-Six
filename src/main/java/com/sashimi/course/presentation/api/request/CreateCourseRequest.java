package com.sashimi.course.presentation.api.request;

import com.sashimi.course.domain.model.CourseDifficulty;
import com.sashimi.course.domain.model.CourseStatus;

import java.util.List;

public record CreateCourseRequest(
        String subCategoryName,
        String title,
        String description,
        Long price,
        CourseDifficulty difficulty,
        String thumbnail,
        Long ncsInfoId,
        CourseStatus initialStatus,
        List<CreateSessionRequest> sessions
) {}
