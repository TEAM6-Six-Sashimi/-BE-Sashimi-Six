package com.sashimi.member.domain.repository;

import com.sashimi.member.domain.model.InstructorApplication;
import com.sashimi.member.domain.model.ApprovalStatus;

import java.util.List;
import java.util.Optional;

public interface InstructorApplicationRepository {

    InstructorApplication save(InstructorApplication instructorApplication);

    Optional<InstructorApplication> findById(Long id);

    Optional<InstructorApplication> findByUserId(Long userId);

    List<InstructorApplication> findAllByStatus(ApprovalStatus status);

    boolean existsByUserIdAndApprovalStatus(Long userId, ApprovalStatus status);
}