package com.sashimi.notice.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.notice.application.command.CreateNoticeCommand;
import com.sashimi.notice.application.port.NoticeRepositoryPort;
import com.sashimi.notice.application.usecase.NoticeCommandUseCase;
import com.sashimi.notice.domain.model.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeCommandService implements NoticeCommandUseCase {

    private final NoticeRepositoryPort noticeRepositoryPort;

    @Override
    public Notice createNotice(CreateNoticeCommand command) {
        Notice notice = Notice.create(
                command.title(),
                command.content(),
                command.pinned()
        );

        return noticeRepositoryPort.save(notice);
    }

    @Override
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepositoryPort.findById(noticeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));

        noticeRepositoryPort.delete(notice);
    }
}