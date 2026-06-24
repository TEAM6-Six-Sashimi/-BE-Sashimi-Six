package com.sashimi.instructorapplication.domain.repository;

import com.sashimi.instructorapplication.domain.model.ApprovalStatus;
import com.sashimi.instructorapplication.domain.model.InstructorApplication;

import java.util.List;
import java.util.Optional;

public interface InstructorApplicationRepository {

    InstructorApplication save(InstructorApplication instructorApplication);

    Optional<InstructorApplication> findById(Long id);

    Optional<InstructorApplication> findByUserId(Long userId);

    List<InstructorApplication> findAllByUserId(Long userId);

    List<InstructorApplication> findAllByStatus(ApprovalStatus status);

    boolean existsByUserIdAndApprovalStatus(Long userId, ApprovalStatus status);
}
