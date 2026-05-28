package com.sashimi.member.presentation;

import com.sashimi.member.application.command.DeleteCertificateCommand;
import com.sashimi.member.application.command.RegisterCertificateCommand;
import com.sashimi.member.application.usecase.CertificateCommandUseCase;
import com.sashimi.member.presentation.api.response.ApiResponse;
import com.sashimi.member.presentation.api.response.CertificateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateCommandUseCase certificateCommandUseCase;

    @PostMapping(value = "/{userId}/certificates", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CertificateResponse>> registerCertificate(
            @PathVariable Long userId,
            @RequestPart("file") MultipartFile file
    ) throws Exception {
        CertificateResponse response = certificateCommandUseCase.registerCertificate(
                new RegisterCertificateCommand(
                        userId,
                        file.getBytes(),
                        file.getOriginalFilename()
                )
        );
        return ResponseEntity.ok(ApiResponse.of("자격증이 등록되었습니다.", response));
    }

    @DeleteMapping("/{userId}/certificates/{certificationId}")
    public ResponseEntity<ApiResponse<Void>> deleteCertificate(
            @PathVariable Long userId,
            @PathVariable Long certificationId
    ) {
        certificateCommandUseCase.deleteCertificate(
                new DeleteCertificateCommand(userId, certificationId)
        );
        return ResponseEntity.ok(ApiResponse.of("자격증이 삭제되었습니다."));
    }
}