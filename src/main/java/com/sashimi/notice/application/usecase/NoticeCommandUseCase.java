package com.sashimi.notice.application.usecase;

import com.sashimi.notice.application.command.CreateNoticeCommand;
import com.sashimi.notice.domain.model.Notice;

public interface NoticeCommandUseCase {

    Notice createNotice(CreateNoticeCommand command);

    void deleteNotice(Long noticeId);
}