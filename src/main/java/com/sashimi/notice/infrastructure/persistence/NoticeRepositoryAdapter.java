package com.sashimi.notice.infrastructure.persistence;

import com.sashimi.notice.application.port.NoticeRepositoryPort;
import com.sashimi.notice.domain.model.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryAdapter implements NoticeRepositoryPort {

    private final SpringDataNoticeRepository springDataNoticeRepository;

    @Override
    public Notice save(Notice notice) {
        return springDataNoticeRepository.save(NoticeJpaEntity.from(notice))
                .toDomain();
    }

    @Override
    public Optional<Notice> findById(Long noticeId) {
        return springDataNoticeRepository.findById(noticeId)
                .map(NoticeJpaEntity::toDomain);
    }

    @Override
    public void delete(Notice notice) {
        springDataNoticeRepository.deleteById(notice.getId());
    }

    @Override
    public Page<Notice> findAll(
            int page,
            int size
    ) {
        return springDataNoticeRepository
                .findAllByOrderByPinnedDescCreatedAtDescIdDesc(
                        PageRequest.of(page, size)
                )
                .map(NoticeJpaEntity::toDomain);
    }
}