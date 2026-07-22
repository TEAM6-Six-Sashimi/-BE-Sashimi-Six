package com.sashimi.instructorapplication.domain.repository;

import com.sashimi.instructorapplication.domain.model.ApprovalStatus;
import com.sashimi.instructorapplication.domain.model.InstructorApplication;
import com.sashimi.instructorapplication.domain.model.VerificationStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InstructorApplicationRepository {

    InstructorApplication save(InstructorApplication instructorApplication);

    Optional<InstructorApplication> findById(Long id);

    Optional<InstructorApplication> findApprovedByUserId(Long userId);

    List<InstructorApplication> findAllByUserId(Long userId);

    List<InstructorApplication> findAllByStatus(ApprovalStatus status);

    boolean existsByUserIdAndApprovalStatus(Long userId, ApprovalStatus status);

    Optional<Long> findUserIdByFileKey(String fileKey);

    List<PendingCertification> findAllPendingCertificationsWithNumber();

    void markCertificationsSubmitted(List<Long> certificationIds);

    Map<Long, VerificationStatus> findVerificationStatusesByApplicationIds(List<Long> applicationIds);
}
