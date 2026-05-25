package com.sashimi.member.presentation;

import com.sashimi.member.application.command.ApplyInstructorCommand;
import com.sashimi.member.application.usecase.MemberCommandUseCase;
import com.sashimi.member.presentation.api.request.ApplyInstructorRequest;
import com.sashimi.member.presentation.api.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberCommandUseCase memberCommandUseCase;

    // 강사 신청
    @PostMapping("/{userId}/instructor-apply")
    public ResponseEntity<ApiResponse<Void>> applyInstructor(
            @PathVariable Long userId,
            @RequestBody ApplyInstructorRequest request
    ) {
        memberCommandUseCase.applyInstructor(
                new ApplyInstructorCommand(userId, request.bio(), request.career(), request.portfolioUrl())
        );
        return ResponseEntity.ok(
                ApiResponse.of("강사 지원이 완료되었습니다. 결과는 사내 평가 후 이메일을 통해 일주일 이내에 발송됩니다.")
        );
    }

    // 강사 승인 (관리자)
    @PatchMapping("/instructor-applications/{applicationId}/approve")
    public ResponseEntity<Void> approveInstructor(@PathVariable Long applicationId) {
        memberCommandUseCase.approveInstructor(applicationId);
        return ResponseEntity.ok().build();
    }

    // 강사 반려 (관리자)
    @PatchMapping("/instructor-applications/{applicationId}/reject")
    public ResponseEntity<Void> rejectInstructor(@PathVariable Long applicationId) {
        memberCommandUseCase.rejectInstructor(applicationId);
        return ResponseEntity.ok().build();
    }
}