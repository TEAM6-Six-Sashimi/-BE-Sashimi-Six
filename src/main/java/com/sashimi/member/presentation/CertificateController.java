package com.sashimi.member.presentation;

import com.sashimi.member.application.command.DeleteCertificateCommand;
import com.sashimi.member.application.command.RegisterCertificateCommand;
import com.sashimi.member.application.usecase.CertificateCommandUseCase;
import com.sashimi.member.presentation.api.request.RegisterCertificateRequest;
import com.sashimi.member.presentation.api.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateCommandUseCase certificateCommandUseCase;

    // 자격증 등록
    @PostMapping("/{userId}/certificates")
    public ResponseEntity<ApiResponse<Void>> registerCertificate(
            @PathVariable Long userId,
            @RequestBody RegisterCertificateRequest request
    ) {
        certificateCommandUseCase.registerCertificate(
                new RegisterCertificateCommand(
                        userId,
                        request.certificationName(),
                        request.issuedBy(),
                        request.issuedDate(),
                        request.fileUrl()
                )
        );
        return ResponseEntity.ok(ApiResponse.of("자격증이 등록되었습니다."));
    }

    // 자격증 삭제
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