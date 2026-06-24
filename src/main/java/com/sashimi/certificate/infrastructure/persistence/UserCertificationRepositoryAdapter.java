package com.sashimi.certificate.infrastructure.persistence;

import com.sashimi.certificate.domain.model.UserCertification;
import com.sashimi.certificate.domain.repository.UserCertificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserCertificationRepositoryAdapter implements UserCertificationRepository {

    private final SpringDataUserCertificationRepository springDataRepository;

    @Override
    public UserCertification save(UserCertification userCertification) {
        UserCertificationJpaEntity entity = UserCertificationJpaEntity.from(userCertification);
        return springDataRepository.save(entity).toDomain();
    }

    @Override
    public Optional<UserCertification> findById(Long id) {
        return springDataRepository.findById(id)
                .map(UserCertificationJpaEntity::toDomain);
    }

    @Override
    public List<UserCertification> findAllByUserId(Long userId) {
        return springDataRepository.findAllByUserId(userId)
                .stream()
                .map(UserCertificationJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UserCertification userCertification) {
        UserCertificationJpaEntity entity = UserCertificationJpaEntity.from(userCertification);
        springDataRepository.save(entity);
    }
}
