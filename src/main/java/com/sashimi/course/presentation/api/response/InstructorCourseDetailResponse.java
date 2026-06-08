package com.sashimi.course.presentation.api.response;

import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseDifficulty;
import com.sashimi.course.domain.model.CourseStatus;

import java.util.List;

public record InstructorCourseDetailResponse(
        Long categoryId,
        String title,
        String description,
        Long price,
        CourseDifficulty difficulty,
        String thumbnail,
        CourseStatus status,
        List<SessionResponse> sessions
) {
    public record SessionResponse(
            String title,
            String videoUrl,
            boolean preview
    ) {}

    public static InstructorCourseDetailResponse from(Course course) {
        List<SessionResponse> sessions = course.getSessions().stream()
                .map(s -> new SessionResponse(s.getTitle(), s.getVideoUrl(), s.isPreview()))
                .toList();
        return new InstructorCourseDetailResponse(
                course.getCategoryId(), course.getTitle(), course.getDescription(),
                course.getPrice(), course.getDifficulty(), course.getThumbnail(),
                course.getStatus(), sessions
        );
    }
}