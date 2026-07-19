package com.sashimi.instructorapplication.infrastructure.persistence;

import com.sashimi.instructorapplication.domain.model.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataInstructorCertificationRepository
        extends JpaRepository<InstructorCertificationJpaEntity, Long> {

    @Query("SELECT c FROM InstructorCertificationJpaEntity c " +
            "WHERE c.verificationStatus = :status AND c.certificationNumber IS NOT NULL")
    List<InstructorCertificationJpaEntity> findAllByVerificationStatusAndCertificationNumberIsNotNull(
            @Param("status") VerificationStatus status);

    @Modifying
    @Query("UPDATE InstructorCertificationJpaEntity c SET c.verificationStatus = :status WHERE c.id IN :ids")
    void updateVerificationStatus(@Param("ids") List<Long> ids, @Param("status") VerificationStatus status);
}
