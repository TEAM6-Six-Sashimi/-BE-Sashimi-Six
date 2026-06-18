package com.sashimi.member.infrastructure.persistence;

import com.sashimi.member.domain.model.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataInstructorApplicationRepository
        extends JpaRepository<InstructorApplicationJpaEntity, Long> {

    Optional<InstructorApplicationJpaEntity> findByUserId(Long userId);

    List<InstructorApplicationJpaEntity> findAllByUserId(Long userId);

    List<InstructorApplicationJpaEntity> findAllByApprovalStatus(ApprovalStatus approvalStatus);

    boolean existsByUserIdAndApprovalStatus(Long userId, ApprovalStatus approvalStatus);
}