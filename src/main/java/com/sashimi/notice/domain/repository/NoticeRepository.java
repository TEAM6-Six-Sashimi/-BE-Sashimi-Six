package com.sashimi.notice.domain.repository;

import com.sashimi.notice.domain.model.Notice;

import java.util.Optional;

public interface NoticeRepository {

    Notice save(Notice notice);

    Optional<Notice> findById(Long noticeId);

    void delete(Notice notice);
}