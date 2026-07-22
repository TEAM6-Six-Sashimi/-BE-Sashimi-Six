package com.sashimi.coverletter.infrastructure.persistence;

import com.sashimi.coverletter.domain.model.CoverLetterQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataCoverLetterRepository
        extends JpaRepository<CoverLetterJpaEntity, Long> {

    List<CoverLetterJpaEntity> findAllByUserId(
            Long userId
    );

    Optional<CoverLetterJpaEntity> findByUserIdAndQuestionKey(
            Long userId,
            CoverLetterQuestion questionKey
    );
}