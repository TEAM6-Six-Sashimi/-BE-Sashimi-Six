package com.sashimi.member.presentation;

import com.sashimi.member.application.command.ApplyInstructorCommand;
import com.sashimi.member.application.usecase.MemberCommandUseCase;
import com.sashimi.member.application.usecase.MemberQueryUseCase;
import com.sashimi.member.presentation.api.request.RejectInstructorRequest;
import com.sashimi.member.presentation.api.response.ApiResponse;
import com.sashimi.member.presentation.api.response.InstructorApplicationDetailResponse;
import com.sashimi.member.presentation.api.response.InstructorApplicationListResponse;
import com.sashimi.member.presentation.api.response.MyInstructorApplicationDetailResponse;
import com.sashimi.member.presentation.api.response.MyInstructorApplicationListResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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

    @Operation(summary = "이력서 양식 다운로드", description = "강사 지원 시 제출할 이력서 양식 파일(docx)을 다운로드합니다.")
    @GetMapping("/resume-template")
    public ResponseEntity<Resource> downloadResumeTemplate() {
        ClassPathResource resource = new ClassPathResource("static/resume/강사지원_이력서_양식.docx");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''%EA%B0%95%EC%82%AC%EC%A7%80%EC%9B%90_%EC%9D%B4%EB%A0%A5%EC%84%9C_%EC%96%91%EC%8B%9D.docx")
                .body(resource);
    }

    @Operation(summary = "강사 지원", description = "자격증 OCR 검증 및 이력서 주요이력 추출 후 강사 신청을 등록합니다.")
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
            @RequestPart("motivationLetter") String motivationLetter,
            @RequestPart("categoryId") String categoryId,
            @RequestPart("portfolioUrl") String portfolioUrl,
            @RequestPart("profileImage") MultipartFile profileImage,
            @RequestPart("certificateFiles") List<MultipartFile> certificateFiles,
            @RequestPart("resumeFile") MultipartFile resumeFile
    ) throws Exception {
        List<ApplyInstructorCommand.FileEntry> certEntries = new java.util.ArrayList<>();
        for (MultipartFile file : certificateFiles) {
            certEntries.add(new ApplyInstructorCommand.FileEntry(file.getBytes(), file.getOriginalFilename()));
        }
        memberCommandUseCase.applyInstructor(
                new ApplyInstructorCommand(
                        userId,
                        bio,
                        motivationLetter,
                        Long.parseLong(categoryId),
                        portfolioUrl,
                        new ApplyInstructorCommand.FileEntry(profileImage.getBytes(), profileImage.getOriginalFilename()),
                        certEntries,
                        new ApplyInstructorCommand.FileEntry(resumeFile.getBytes(), resumeFile.getOriginalFilename())
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
    public ResponseEntity<ApiResponse<Void>> rejectInstructor(
            @PathVariable Long applicationId,
            @Valid @RequestBody RejectInstructorRequest request) {
        memberCommandUseCase.rejectInstructor(applicationId, request.rejectionCategory(), request.rejectionReason());
        return ResponseEntity.ok(ApiResponse.of("강사 요청을 반려했습니다."));
    }

    @Operation(summary = "나의 강사 지원 내역 목록 조회", description = "본인의 강사 지원 내역 목록을 조회합니다.")
    @GetMapping("/{userId}/instructor-applications")
    public ResponseEntity<ApiResponse<List<MyInstructorApplicationListResponse>>> getMyInstructorApplications(
            @PathVariable Long userId) {
        List<MyInstructorApplicationListResponse> responses = memberQueryUseCase.getMyInstructorApplications(userId);
        return ResponseEntity.ok(ApiResponse.of("강사 지원 내역 조회 성공", responses));
    }

    @Operation(summary = "나의 강사 지원 상세 조회", description = "본인의 강사 지원 상세 정보 및 반려 사유를 조회합니다.")
    @GetMapping("/{userId}/instructor-applications/{applicationId}")
    public ResponseEntity<ApiResponse<MyInstructorApplicationDetailResponse>> getMyInstructorApplicationDetail(
            @PathVariable Long userId,
            @PathVariable Long applicationId) {
        MyInstructorApplicationDetailResponse response = memberQueryUseCase.getMyInstructorApplicationDetail(userId, applicationId);
        return ResponseEntity.ok(ApiResponse.of("강사 지원 상세 조회 성공", response));
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