package com.sashimi.resume.infrastructure.persistence;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.repository.ResumeRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ResumeRepositoryAdapter
        implements ResumeRepository {

    private final SpringDataResumeRepository
            springDataRepository;

    public ResumeRepositoryAdapter(
            SpringDataResumeRepository springDataRepository
    ) {
        this.springDataRepository =
                springDataRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Resume> findByIdAndUserId(
            Long resumeId,
            Long userId
    ) {
        return springDataRepository
                .findByResumeIdAndUserId(
                        resumeId,
                        userId
                )
                .map(ResumeJpaEntity::toDomain);
    }

    @Override
    public Resume save(Resume resume) {
        if (resume.resumeId() == null) {
            ResumeJpaEntity newEntity =
                    ResumeJpaEntity.from(resume);

            ResumeJpaEntity savedEntity =
                    springDataRepository.save(
                            newEntity
                    );

            return savedEntity.toDomain();
        }

        ResumeJpaEntity existingEntity =
                springDataRepository
                        .findByResumeIdAndUserId(
                                resume.resumeId(),
                                resume.userId()
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode
                                                .RESUME_NOT_FOUND
                                )
                        );

        existingEntity.updateFrom(resume);

        springDataRepository.flush();

        return existingEntity.toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resume> findAllByUserId(
            Long userId
    ) {
        return springDataRepository
                .findAllByUserIdOrderByCreatedAtDesc(
                        userId
                )
                .stream()
                .map(ResumeJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void delete(Resume resume) {
        springDataRepository.deleteByResumeIdAndUserId(
                resume.resumeId(),
                resume.userId()
        );
    }
}