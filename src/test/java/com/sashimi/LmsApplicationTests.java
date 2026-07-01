package com.sashimi;

import com.sashimi.certificate.infrastructure.CodefTokenManager;
import com.sashimi.global.storage.FileStoragePort;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzePort;
import com.sashimi.verification.application.port.EmailSender;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles({"test", "gemini"})
class LmsApplicationTests {

    @MockitoBean
    private EmailSender emailSender;

    @MockitoBean
    private JobPostingRecommendationAnalyzePort jobPostingRecommendationAnalyzePort;

    @MockitoBean
    private CodefTokenManager codefTokenManager;

    @MockitoBean
    private FileStoragePort fileStoragePort;

    @Test
    void contextLoads() {
    }

}
