package com.sashimi.architecture;

import com.sashimi.certificate.infrastructure.CodefTokenManager;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzePort;
import com.sashimi.user.domain.repository.UserRepository;
import com.sashimi.user.infrastructure.persistence.UserRepositoryAdapter;
import com.sashimi.verification.application.port.EmailSender;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles({"test", "gemini"})
@Transactional
@DisplayName("[계약 테스트] JpaUserRepositoryAdapter - H2 실제 JPA 환경")
class JpaUserRepositoryContractTest extends UserRepositoryContractTest {

    @MockitoBean
    private EmailSender emailSender;

    @MockitoBean
    private JobPostingRecommendationAnalyzePort jobPostingRecommendationAnalyzePort;

    @MockitoBean
    private CodefTokenManager codefTokenManager;

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @Override
    protected UserRepository createRepository() {
        return userRepositoryAdapter;
    }
}
