package com.sashimi.member.domain.repository;

import com.sashimi.member.domain.model.UserCertification;

import java.util.List;
import java.util.Optional;

public interface UserCertificationRepository {

    UserCertification save(UserCertification userCertification);

    Optional<UserCertification> findById(Long id);

    List<UserCertification> findAllByUserId(Long userId);

    void delete(UserCertification userCertification);
}