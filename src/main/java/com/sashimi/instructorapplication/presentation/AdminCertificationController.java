package com.sashimi.instructorapplication.presentation;

import com.sashimi.instructorapplication.application.usecase.InstructorApplicationCommandUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자 자격증 진위검증", description = "자격증 진위검증 큐넷 제출용 엑셀 다운로드 API (ROLE_ADMIN 전용)")
@RestController
@RequestMapping("/admin/verification")
@RequiredArgsConstructor
public class AdminCertificationController {

    private final InstructorApplicationCommandUseCase instructorApplicationCommandUseCase;

    @Operation(
            summary = "자격증 진위검증 엑셀 다운로드",
            description = "검증대기 상태인 자격증들의 성명+자격증번호를 큐넷 제출 양식에 맞는 엑셀로 생성해 다운로드한다. " +
                    "다운로드 성공 시 해당 건들의 상태가 제출됨으로 변경된다."
    )
    @GetMapping("/excel")
    public ResponseEntity<byte[]> downloadVerificationExcel() {
        byte[] excel = instructorApplicationCommandUseCase.generateVerificationExcel();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"verification.xlsx\"")
                .body(excel);
    }
}
