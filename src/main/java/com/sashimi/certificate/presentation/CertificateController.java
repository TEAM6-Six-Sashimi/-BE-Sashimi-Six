package com.sashimi.certificate.presentation;

import com.sashimi.certificate.application.command.DeleteCertificateCommand;
import com.sashimi.certificate.application.command.RegisterCertificateCommand;
import com.sashimi.certificate.application.usecase.CertificateCommandUseCase;
import com.sashimi.certificate.application.usecase.CertificateQueryUseCase;
import com.sashimi.certificate.presentation.api.response.CertificateResponse;
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
@Tag(name = "자격증 API", description = "자격증 등록, 삭제, 목록 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class CertificateController {

    private final CertificateCommandUseCase certificateCommandUseCase;
    private final CertificateQueryUseCase certificateQueryUseCase;

    @Operation(summary = "자격증 등록", description = "자격증 파일을 OCR로 검증 후 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "자격증 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "OCR 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/{userId}/certificates", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<CertificateResponse>> registerCertificates(
            @PathVariable Long userId,
            @RequestPart("files") List<MultipartFile> files
    ) throws Exception {
        List<RegisterCertificateCommand.FileEntry> fileEntries = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            fileEntries.add(new RegisterCertificateCommand.FileEntry(
                    file.getBytes(),
                    file.getOriginalFilename()
            ));
        }

        List<CertificateResponse> responses = certificateCommandUseCase.registerCertificates(
                new RegisterCertificateCommand(userId, fileEntries)
        );
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "자격증 삭제", description = "등록된 자격증을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "자격증 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "자격증을 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{userId}/certificates/{certificationId}")
    public ResponseEntity<Void> deleteCertificate(
            @PathVariable Long userId,
            @PathVariable Long certificationId
    ) {
        certificateCommandUseCase.deleteCertificate(
                new DeleteCertificateCommand(userId, certificationId)
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "자격증 목록 조회", description = "사용자의 자격증 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{userId}/certificates")
    public ResponseEntity<List<CertificateResponse>> getCertificates(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(certificateQueryUseCase.getCertificates(userId));
    }
}
