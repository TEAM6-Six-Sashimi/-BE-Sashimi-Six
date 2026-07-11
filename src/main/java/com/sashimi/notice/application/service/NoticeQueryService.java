package com.sashimi.notice.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.notice.application.port.NoticeRepositoryPort;
import com.sashimi.notice.application.usecase.NoticeQueryUseCase;
import com.sashimi.notice.domain.model.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeQueryService implements NoticeQueryUseCase {

    private static final int MAX_PAGE_SIZE = 500;

    private final NoticeRepositoryPort noticeRepositoryPort;

    @Override
    public Page<Notice> getNotices(
            int page,
            int size
    ) {
        validatePageRequest(page, size);
        return noticeRepositoryPort.findAll(page, size);
    }

    @Override
    public Notice getNotice(Long noticeId) {
        return noticeRepositoryPort.findById(noticeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));
    }

    private void validatePageRequest(
            int page,
            int size
    ) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
            throw new BusinessException(ErrorCode.NOTICE_PAGE_SIZE_INVALID);
        }
    }
}