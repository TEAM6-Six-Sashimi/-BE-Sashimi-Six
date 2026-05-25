package com.sashimi.course.presentation.api.response;

import com.sashimi.course.domain.model.CourseSession;

public record CourseSessionResponse(
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
) {
    public static CourseSessionResponse from(CourseSession session) {
        return new CourseSessionResponse(
                session.getId(), session.getSessionUid(), session.getTitle(),
                session.getVideoUrl(), session.getDurationSeconds(), session.getSessionOrder(),
                session.isPreview(), session.getAttachmentName(), session.getAttachmentUrl(),
                session.getAttachmentType(), session.getAttachmentSize()
        );
    }
}
