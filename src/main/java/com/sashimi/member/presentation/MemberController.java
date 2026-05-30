package com.sashimi.member.presentation;

import com.sashimi.member.application.command.ApplyInstructorCommand;
import com.sashimi.member.application.usecase.MemberCommandUseCase;
import com.sashimi.member.application.usecase.MemberQueryUseCase;
import com.sashimi.member.presentation.api.response.ApiResponse;
import com.sashimi.member.presentation.api.response.InstructorApplicationDetailResponse;
import com.sashimi.member.presentation.api.response.InstructorApplicationListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "강사 신청 API", description = "강사 신청, 승인, 반려, 대기 목록 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class MemberController {

    private final MemberCommandUseCase memberCommandUseCase;
    private final MemberQueryUseCase memberQueryUseCase;

    @Operation(summary = "강사 지원", description = "자격증 파일을 OCR로 검증 후 강사 신청을 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "강사 지원 완료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 오류 / 중복 신청 / OCR 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
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

    @Operation(summary = "강사 신청 승인 [ADMIN 전용]", description = "관리자만 강사 신청을 승인할 수 있습니다. ROLE_ADMIN 권한 필요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "승인 완료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "처리할 수 없는 신청 상태"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음 (ROLE_ADMIN 아닌 경우)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "신청을 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping("/instructor-applications/{applicationId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveInstructor(@PathVariable Long applicationId) {
        memberCommandUseCase.approveInstructor(applicationId);
        return ResponseEntity.ok(ApiResponse.of("강사 요청을 승인했습니다."));
    }

    @Operation(summary = "강사 신청 반려 [ADMIN 전용]", description = "관리자만 강사 신청을 반려할 수 있습니다. ROLE_ADMIN 권한 필요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "반려 완료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "처리할 수 없는 신청 상태"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음 (ROLE_ADMIN 아닌 경우)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "신청을 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping("/instructor-applications/{applicationId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectInstructor(@PathVariable Long applicationId) {
        memberCommandUseCase.rejectInstructor(applicationId);
        return ResponseEntity.ok(ApiResponse.of("강사 요청을 반려했습니다."));
    }

    @Operation(summary = "강사 신청 대기 목록 조회 [ADMIN 전용]", description = "관리자만 승인 대기 중인 강사 신청 목록을 조회할 수 있습니다. ROLE_ADMIN 권한 필요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음 (ROLE_ADMIN 아닌 경우)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/instructor-applications/pending")
    public ResponseEntity<ApiResponse<List<InstructorApplicationListResponse>>> getPendingInstructorApplications() {
        List<InstructorApplicationListResponse> responses = memberQueryUseCase.getPendingInstructorApplications();
        return ResponseEntity.ok(ApiResponse.of("강사 신청 대기 목록 조회 성공", responses));
    }

    @Operation(summary = "강사 신청 상세 조회 [ADMIN 전용]", description = "관리자만 강사 신청 상세 정보를 조회할 수 있습니다. ROLE_ADMIN 권한 필요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음 (ROLE_ADMIN 아닌 경우)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "신청을 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/instructor-applications/{applicationId}")
    public ResponseEntity<ApiResponse<InstructorApplicationDetailResponse>> getInstructorApplicationDetail(
            @PathVariable Long applicationId) {
        InstructorApplicationDetailResponse response = memberQueryUseCase.getInstructorApplicationDetail(applicationId);
        return ResponseEntity.ok(ApiResponse.of("강사 신청 상세 조회 성공", response));
    }
}