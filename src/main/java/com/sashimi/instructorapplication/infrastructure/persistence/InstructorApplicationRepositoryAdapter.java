package com.sashimi.instructorapplication.infrastructure.persistence;

import com.sashimi.instructorapplication.domain.model.ApprovalStatus;
import com.sashimi.instructorapplication.domain.model.InstructorApplication;
import com.sashimi.instructorapplication.domain.model.VerificationStatus;
import com.sashimi.instructorapplication.domain.repository.InstructorApplicationRepository;
import com.sashimi.instructorapplication.domain.repository.PendingCertification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InstructorApplicationRepositoryAdapter implements InstructorApplicationRepository {

    private final SpringDataInstructorApplicationRepository springDataRepository;
    private final SpringDataInstructorCertificationRepository certificationRepository;

    @Override
    public InstructorApplication save(InstructorApplication instructorApplication) {
        InstructorApplicationJpaEntity entity = InstructorApplicationJpaEntity.from(instructorApplication);
        return springDataRepository.save(entity).toDomain();
    }

    @Override
    public Optional<InstructorApplication> findById(Long id) {
        return springDataRepository.findById(id)
                .map(InstructorApplicationJpaEntity::toDomain);
    }

    @Override
    public Optional<InstructorApplication> findApprovedByUserId(Long userId) {
        return springDataRepository.findFirstByUserIdAndApprovalStatusOrderByApprovedAtDesc(userId, ApprovalStatus.APPROVED)
                .map(InstructorApplicationJpaEntity::toDomain);
    }

    @Override
    public List<InstructorApplication> findAllByUserId(Long userId) {
        return springDataRepository.findAllByUserId(userId)
                .stream()
                .map(InstructorApplicationJpaEntity::toDomainWithoutCertifications)
                .collect(Collectors.toList());
    }

    @Override
    public List<InstructorApplication> findAllByStatus(ApprovalStatus status) {
        return springDataRepository.findAllByApprovalStatus(status)
                .stream()
                .map(InstructorApplicationJpaEntity::toDomainWithoutCertifications)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUserIdAndApprovalStatus(Long userId, ApprovalStatus status) {
        return springDataRepository.existsByUserIdAndApprovalStatus(userId, status);
    }

    @Override
    public Optional<Long> findUserIdByFileKey(String fileKey) {
        return springDataRepository.findUserIdByFileKey(fileKey);
    }

    @Override
    public List<PendingCertification> findAllPendingCertificationsWithNumber() {
        return certificationRepository
                .findAllByVerificationStatusAndCertificationNumberIsNotNull(VerificationStatus.PENDING)
                .stream()
                .map(c -> new PendingCertification(c.getId(), c.getApplication().getUserId(), c.getCertificationNumber()))
                .collect(Collectors.toList());
    }

    @Override
    public void markCertificationsSubmitted(List<Long> certificationIds) {
        certificationRepository.updateVerificationStatus(certificationIds, VerificationStatus.SUBMITTED);
    }
}
