package com.sashimi.notice.presentation.api.response;

import com.sashimi.notice.domain.model.Notice;
import org.springframework.data.domain.Page;

import java.util.List;

public record NoticeListResponse(
        List<NoticeSummaryResponse> items,
        int page,
        int size,
        int totalPages,
        long totalElements
) {

    public static NoticeListResponse from(Page<Notice> notices) {
        return new NoticeListResponse(
                notices.getContent()
                        .stream()
                        .map(NoticeSummaryResponse::from)
                        .toList(),
                notices.getNumber(),
                notices.getSize(),
                notices.getTotalPages(),
                notices.getTotalElements()
        );
    }
}