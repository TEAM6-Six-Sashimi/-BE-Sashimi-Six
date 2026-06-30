package com.sashimi.course.presentation.api.response;

import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseDifficulty;
import com.sashimi.course.domain.model.CourseStatus;
import com.sashimi.global.storage.FileStoragePort;

import java.util.List;

public record InstructorCourseDetailResponse(
        Long courseId,
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
            Long sessionId,
            String sessionUid,
            String title,
            String videoUrl,
            int durationSeconds,
            int sessionOrder,
            boolean preview,
            String attachmentName,
            String attachmentUrl,
            String attachmentType,
            Long attachmentSize
    ) {}

    /** 영상 시청 presigned URL 만료 (분) */
    private static final int VIDEO_URL_EXPIRY_MINUTES = 120;
    /** 자료 다운로드 presigned URL 만료 (분) */
    private static final int ATTACHMENT_URL_EXPIRY_MINUTES = 120;

    public static InstructorCourseDetailResponse from(Course course, FileStoragePort fileStoragePort) {
        List<SessionResponse> sessions = course.getSessions().stream()
                .map(s -> new SessionResponse(
                        s.getId(), s.getSessionUid(), s.getTitle(),
                        resolveVideoUrl(fileStoragePort, s.getVideoUrl()),
                        s.getDurationSeconds(), s.getSessionOrder(), s.isPreview(),
                        s.getAttachmentName(),
                        resolveAttachmentUrl(fileStoragePort, s.getAttachmentUrl()),
                        s.getAttachmentType(), s.getAttachmentSize()
                ))
                .toList();
        return new InstructorCourseDetailResponse(
                course.getId(), course.getCategoryId(), course.getTitle(), course.getDescription(),
                course.getPrice(), course.getDifficulty(), course.getThumbnail(),
                course.getStatus(), sessions
        );
    }

    private static String resolveVideoUrl(FileStoragePort fileStoragePort, String key) {
        if (key == null || key.isBlank() || key.startsWith("http")) {
            return key;
        }
        return fileStoragePort.generateVideoUrl(key, VIDEO_URL_EXPIRY_MINUTES);
    }

    private static String resolveAttachmentUrl(FileStoragePort fileStoragePort, String key) {
        if (key == null || key.isBlank() || key.startsWith("http")) {
            return key;
        }
        return fileStoragePort.generateAttachmentUrl(key, ATTACHMENT_URL_EXPIRY_MINUTES);
    }
}