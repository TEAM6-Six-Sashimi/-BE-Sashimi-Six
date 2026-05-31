package com.sashimi;

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

    @Test
    void contextLoads() {
    }

}
