package com.sashimi.credit.domain.repository;

import com.sashimi.credit.domain.model.Credit;

import java.util.Optional;

public interface CreditRepository {

    Optional<Credit> findByUserId(Long userId);

    Credit save(Credit credit);
}