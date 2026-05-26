package com.sashimi.recommendation.presentation.api;

import com.sashimi.recommendation.application.command.CreateJobPostingRecommendationCommand;
import com.sashimi.recommendation.application.usecase.JobPostingRecommendationCommandUseCase;
import com.sashimi.recommendation.application.usecase.JobPostingRecommendationQueryUseCase;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.presentation.api.request.CreateJobPostingRecommendationRequest;
import com.sashimi.recommendation.presentation.api.response.CertificateRecommendationResponse;
import com.sashimi.recommendation.presentation.api.response.CourseRecommendationResponse;
import com.sashimi.recommendation.presentation.api.response.JobPostingRecommendationResponse;
import com.sashimi.recommendation.presentation.api.response.RequiredSkillRecommendationResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations/job-posting")
public class JobPostingRecommendationController {

    private final JobPostingRecommendationCommandUseCase commandUseCase;
    private final JobPostingRecommendationQueryUseCase queryUseCase;

    public JobPostingRecommendationController(
            JobPostingRecommendationCommandUseCase commandUseCase,
            JobPostingRecommendationQueryUseCase queryUseCase
    ) {
        this.commandUseCase = commandUseCase;
        this.queryUseCase = queryUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobPostingRecommendationResponse create(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody CreateJobPostingRecommendationRequest request
    ) {
        JobPostingRecommendation recommendation = commandUseCase.create(
                new CreateJobPostingRecommendationCommand(
                        principal.getId(),
                        request.inputType(),
                        request.sourceUrl(),
                        request.rawContent()
                )
        );

        return JobPostingRecommendationResponse.from(recommendation);
    }

    @GetMapping
    public JobPostingRecommendationResponse getLatest(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        JobPostingRecommendation recommendation = queryUseCase.getLatest(principal.getId());
        return JobPostingRecommendationResponse.from(recommendation);
    }

    @GetMapping("/skills")
    public List<RequiredSkillRecommendationResponse> getSkills(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return queryUseCase.getLatestSkills(principal.getId()).stream()
                .map(RequiredSkillRecommendationResponse::from)
                .toList();
    }

    @PostMapping("/courses")
    public List<CourseRecommendationResponse> recommendCourses(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return queryUseCase.getLatestCourses(principal.getId()).stream()
                .map(CourseRecommendationResponse::from)
                .toList();
    }

    @GetMapping("/certificates")
    public List<CertificateRecommendationResponse> getCertificates(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return queryUseCase.getLatestCertificates(principal.getId()).stream()
                .map(CertificateRecommendationResponse::from)
                .toList();
    }
}
