package com.sashimi.credit.infrastructure.persistence;

import com.sashimi.credit.domain.model.Credit;
import com.sashimi.credit.domain.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CreditRepositoryAdapter implements CreditRepository {

    private final SpringDataCreditRepository springDataCreditRepository;

    @Override
    public Optional<Credit> findByUserId(Long userId) {
        return springDataCreditRepository.findByUserId(userId)
                .map(CreditJpaEntity::toDomain);
    }

    @Override
    public Credit save(Credit credit) {
        return springDataCreditRepository.save(CreditJpaEntity.from(credit)).toDomain();
    }

    @Override
    public Optional<Credit> findByUserIdForUpdate(Long userId) {
        return springDataCreditRepository.findByUserIdForUpdate(userId)
                .map(CreditJpaEntity::toDomain);
    }


}
