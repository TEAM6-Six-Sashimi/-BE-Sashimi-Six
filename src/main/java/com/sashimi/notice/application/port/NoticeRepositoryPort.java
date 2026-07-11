package com.sashimi.notice.application.port;

import com.sashimi.notice.domain.model.Notice;
import com.sashimi.notice.domain.repository.NoticeRepository;
import org.springframework.data.domain.Page;

public interface NoticeRepositoryPort extends NoticeRepository {

    Page<Notice> findAll(
            int page,
            int size
    );
}