package com.sashimi.verification.presentation.api;

import com.sashimi.security.principal.CustomUserPrincipal;
import com.sashimi.verification.application.usecase.EmailVerificationUseCase;
import com.sashimi.verification.presentation.api.request.ConfirmEmailVerificationRequest;
import com.sashimi.verification.presentation.api.request.EmailVerificationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.sashimi.verification.presentation.api.response.EmailVerificationConfirmResult;
import com.sashimi.verification.presentation.api.response.EmailVerificationRequestResult;
import com.sashimi.verification.presentation.api.response.EmailVerificationConfirmResponse;
import com.sashimi.verification.presentation.api.response.EmailVerificationRequestResponse;

@RestController
@RequestMapping("/verifications/email")
@RequiredArgsConstructor
@Tag(name = "이메일 인증 API", description = "회원가입, 비밀번호 재설정, 이메일 변경을 위한 이메일 인증 API")
public class EmailVerificationController {

    private final EmailVerificationUseCase emailVerificationUseCase;

    @Operation(summary = "이메일 인증 코드 요청", description = "인증 목적에 따라 이메일 인증 코드를 발송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 인증 코드 요청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 이메일 또는 인증 목적"),
            @ApiResponse(responseCode = "429", description = "이메일 인증 코드 재요청 제한"),
            @ApiResponse(responseCode = "500", description = "이메일 발송 실패 또는 서버 오류")
    })
    @PostMapping("/request")
    public ResponseEntity<EmailVerificationRequestResponse> requestEmailVerification(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid EmailVerificationRequest request
    ) {
        Long userId = principal == null ? null : principal.getId();

        EmailVerificationRequestResult result = emailVerificationUseCase.requestEmailVerification(
                request.toCommand(userId)
        );

        return ResponseEntity.ok(new EmailVerificationRequestResponse(
                result.targetEmail(),
                result.purpose(),
                result.expiresInSeconds(),
                result.resendAvailableInSeconds()
        ));
    }


    @Operation(summary = "이메일 인증 확인", description = "이메일로 발송된 인증 코드를 확인하고 인증 완료 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 인증 성공"),
            @ApiResponse(responseCode = "400", description = "만료된 인증 코드 또는 일치하지 않는 인증 코드"),
            @ApiResponse(responseCode = "404", description = "이메일 인증 요청을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/confirm")
    public ResponseEntity<EmailVerificationConfirmResponse> confirmEmailVerification(
            @RequestBody @Valid ConfirmEmailVerificationRequest request
    ) {
        EmailVerificationConfirmResult result =
                emailVerificationUseCase.confirmEmailVerification(request.toCommand());

        return ResponseEntity.ok(new EmailVerificationConfirmResponse(
                result.targetEmail(),
                result.purpose(),
                result.verified()
        ));
    }
}