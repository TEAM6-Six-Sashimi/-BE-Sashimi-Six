package com.sashimi.notice.presentation.api.response;

import com.sashimi.notice.domain.model.Notice;

import java.time.LocalDate;

public record NoticeSummaryResponse(
        Long noticeId,
        String title,
        boolean pinned,
        LocalDate createdDate
) {

    public static NoticeSummaryResponse from(Notice notice) {
        return new NoticeSummaryResponse(
                notice.getId(),
                notice.getTitle(),
                notice.isPinned(),
                notice.getCreatedAt().toLocalDate()
        );
    }
}