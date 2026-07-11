package com.sashimi.notice.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataNoticeRepository extends JpaRepository<NoticeJpaEntity, Long> {

    Page<NoticeJpaEntity> findAllByOrderByPinnedDescCreatedAtDescIdDesc(Pageable pageable);
}