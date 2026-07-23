package com.sashimi.instructorapplication.infrastructure.persistence;

import com.sashimi.instructorapplication.domain.model.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataInstructorApplicationRepository
        extends JpaRepository<InstructorApplicationJpaEntity, Long> {

    Optional<InstructorApplicationJpaEntity> findFirstByUserIdAndApprovalStatusOrderByApprovedAtDesc(
            Long userId, ApprovalStatus approvalStatus);

    List<InstructorApplicationJpaEntity> findAllByUserId(Long userId);

    List<InstructorApplicationJpaEntity> findAllByApprovalStatus(ApprovalStatus approvalStatus);

    List<InstructorApplicationJpaEntity> findTop500ByApprovalStatusOrderByCreatedAtDesc(ApprovalStatus approvalStatus);

    boolean existsByUserIdAndApprovalStatus(Long userId, ApprovalStatus approvalStatus);

    @Query("SELECT DISTINCT e.userId FROM InstructorApplicationJpaEntity e LEFT JOIN e.certifications c " +
            "WHERE e.profileImagePath = :key OR e.resumeFilePath = :key OR c.filePath = :key")
    Optional<Long> findUserIdByFileKey(@Param("key") String key);
}
