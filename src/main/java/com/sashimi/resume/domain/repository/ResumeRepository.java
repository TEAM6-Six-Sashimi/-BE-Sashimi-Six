package com.sashimi.resume.domain.repository;

import com.sashimi.resume.domain.model.Resume;

import java.util.Optional;

public interface ResumeRepository {

    Resume save(Resume resume);

    Optional<Resume> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    Optional<Resume> findByIdAndUserId(
            Long resumeId,
            Long userId
    );

    void delete(Resume resume);
}