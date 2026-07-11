package com.sashimi.notice.presentation.api.response;

import com.sashimi.notice.domain.model.Notice;

import java.time.LocalDateTime;

public record NoticeDetailResponse(
        Long noticeId,
        String title,
        String content,
        boolean pinned,
        LocalDateTime createdAt
) {

    public static NoticeDetailResponse from(Notice notice) {
        return new NoticeDetailResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.isPinned(),
                notice.getCreatedAt()
        );
    }
}