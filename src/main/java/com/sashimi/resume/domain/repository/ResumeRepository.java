package com.sashimi.resume.domain.repository;

import com.sashimi.resume.domain.model.Resume;

import java.util.List;
import java.util.Optional;

/**
 * 이력서 저장소 Port.
 *
 * 본인 이력서만 조회해야 하므로 resumeId와 userId를 함께 받는다.
 */
public interface ResumeRepository {

    Resume save(Resume resume);

    List<Resume> findAllByUserId(Long userId);
    /**
     * 특정 사용자가 작성한 특정 이력서를 조회한다.
     */
    Optional<Resume> findByIdAndUserId(Long resumeId, Long userId);

    void delete(Resume resume);
}
