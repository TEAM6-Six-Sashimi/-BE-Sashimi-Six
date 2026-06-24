package com.sashimi.certificate.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataUserCertificationRepository
        extends JpaRepository<UserCertificationJpaEntity, Long> {

    List<UserCertificationJpaEntity> findAllByUserId(Long userId);
}
