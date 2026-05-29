package com.sashimi.user.domain.repository;

import com.sashimi.user.infrastructure.persistence.InMemoryUserRepository;
import org.junit.jupiter.api.DisplayName;

@DisplayName("[계약 테스트] InMemoryUserRepository")
class InMemoryUserRepositoryContractTest extends UserRepositoryContractTest {

    @Override
    protected UserRepository createRepository() {
        return new InMemoryUserRepository();
    }
}
