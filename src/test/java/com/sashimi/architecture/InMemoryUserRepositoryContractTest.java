package com.sashimi.architecture;

import com.sashimi.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;

@DisplayName("[계약 테스트] InMemoryUserRepository")
class InMemoryUserRepositoryContractTest extends UserRepositoryContractTest {

    @Override
    protected UserRepository createRepository() {
        return new InMemoryUserRepository();
    }
}
