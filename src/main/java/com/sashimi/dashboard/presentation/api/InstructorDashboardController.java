package com.sashimi.dashboard.presentation.api;

import com.sashimi.security.principal.CustomUserPrincipal;
import com.sashimi.dashboard.application.usecase.InstructorDashboardQueryUseCase;
import com.sashimi.dashboard.presentation.api.response.InstructorDashboardSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instructor/dashboard")
@RequiredArgsConstructor
public class InstructorDashboardController {

    private final InstructorDashboardQueryUseCase instructorDashboardQueryUseCase;

    @GetMapping("/summary")
    public ResponseEntity<InstructorDashboardSummaryResponse> getMonthlySummary(
            @AuthenticationPrincipal CustomUserPrincipal  principal,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        return ResponseEntity.ok(
                InstructorDashboardSummaryResponse.from(
                        instructorDashboardQueryUseCase.getMonthlySummary(
                                principal.getId(),
                                year,
                                month
                        )
                )
        );
    }
}