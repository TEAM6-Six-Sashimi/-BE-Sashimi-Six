package com.sashimi.member.presentation;

import com.sashimi.member.application.command.ApplyInstructorCommand;
import com.sashimi.member.application.usecase.MemberCommandUseCase;
import com.sashimi.member.application.usecase.MemberQueryUseCase;
import com.sashimi.member.presentation.api.response.ApiResponse;
import com.sashimi.member.presentation.api.response.InstructorApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberCommandUseCase memberCommandUseCase;
    private final MemberQueryUseCase memberQueryUseCase;

    @PostMapping(value = "/{userId}/instructor-apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> applyInstructor(
            @PathVariable Long userId,
            @RequestPart("bio") String bio,
            @RequestPart("portfolioUrl") String portfolioUrl,
            @RequestPart("file") MultipartFile file
    ) throws Exception {
        memberCommandUseCase.applyInstructor(
                new ApplyInstructorCommand(
                        userId,
                        bio,
                        portfolioUrl,
                        file.getBytes(),
                        file.getOriginalFilename()
                )
        );
        return ResponseEntity.ok(ApiResponse.of("강사 지원이 완료되었습니다. 결과는 사내 평가 후 이메일을 통해 일주일 이내에 발송됩니다."));
    }

    @PatchMapping("/instructor-applications/{applicationId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveInstructor(@PathVariable Long applicationId) {
        memberCommandUseCase.approveInstructor(applicationId);
        return ResponseEntity.ok(ApiResponse.of("강사 요청을 승인했습니다."));
    }

    @PatchMapping("/instructor-applications/{applicationId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectInstructor(@PathVariable Long applicationId) {
        memberCommandUseCase.rejectInstructor(applicationId);
        return ResponseEntity.ok(ApiResponse.of("강사 요청을 반려했습니다."));
    }

    @GetMapping("/instructor-applications/pending")
    public ResponseEntity<ApiResponse<List<InstructorApplicationResponse>>> getPendingInstructorApplications() {
        List<InstructorApplicationResponse> responses = memberQueryUseCase.getPendingInstructorApplications();
        return ResponseEntity.ok(ApiResponse.of("강사 신청 대기 목록 조회 성공", responses));
    }
}