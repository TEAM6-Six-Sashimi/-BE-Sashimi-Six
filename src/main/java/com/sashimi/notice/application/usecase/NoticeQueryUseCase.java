package com.sashimi.notice.application.usecase;

import com.sashimi.notice.domain.model.Notice;
import org.springframework.data.domain.Page;

public interface NoticeQueryUseCase {

    Page<Notice> getNotices(
            int page,
            int size
    );

    Notice getNotice(Long noticeId);
}