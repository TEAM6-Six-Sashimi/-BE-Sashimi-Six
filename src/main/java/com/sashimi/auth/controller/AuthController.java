package com.sashimi.auth.controller;

import com.sashimi.auth.dto.FindLoginIdConfirmRequestDto;
import com.sashimi.auth.dto.FindLoginIdConfirmResponseDto;
import com.sashimi.auth.dto.FindLoginIdRequestDto;
import com.sashimi.auth.dto.FindLoginIdRequestResponseDto;
import com.sashimi.auth.dto.LoginRequestDto;
import com.sashimi.auth.dto.LogoutRequestDto;
import com.sashimi.auth.dto.ReissueRequestDto;
import com.sashimi.auth.dto.TokenResponseDto;
import com.sashimi.auth.dto.WsTicketResponseDto;
import com.sashimi.auth.service.AuthService;
import com.sashimi.global.web.ClientIpResolver;
import jakarta.servlet.http.HttpServletRequest;
import com.sashimi.user.dto.LoginIdCheckResponseDto;
import com.sashimi.user.dto.ReferralCodeCheckResponseDto;
import com.sashimi.user.dto.SignupRequestDto;
import com.sashimi.user.dto.UserResponseDto;
import com.sashimi.auth.dto.PasswordResetConfirmRequestDto;
import com.sashimi.auth.dto.PasswordResetRequestDto;
import com.sashimi.auth.dto.PasswordResetRequestResponseDto;
import com.sashimi.auth.dto.PasswordResetConfirmResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "회원가입, 로그인, 토큰 재발급, 로그아웃, 비밀번호 재설정 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "아이디 중복 체크", description = "신규 회원 가입 전 가입하려는 아이디가 기존의 회원들과 중복이 아닌지 검증합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아이디 사용 가능 여부 조회 성공"),
            @ApiResponse(responseCode = "400", description = "필수 요청 파라미터 누락 또는 잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/login-id/check")
    public ResponseEntity<LoginIdCheckResponseDto> checkLoginId(@RequestParam String loginId) {
        return ResponseEntity.ok(authService.checkLoginId(loginId));
    }

    @Operation(summary = "추천인 코드 확인", description = "추천인 코드가 유효한지 확인하고 추천인 이름을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추천인 코드 조회 성공"),
            @ApiResponse(responseCode = "400", description = "필수 요청 파라미터 누락 또는 잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/referral-code/check")
    public ResponseEntity<ReferralCodeCheckResponseDto> checkReferralCode(@RequestParam String referralCode) {
        return ResponseEntity.ok(authService.checkReferralCode(referralCode));
    }

    @Operation(summary = "회원가입", description = "이메일 인증 완료 후 신규 회원을 가입시킵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 이메일 미인증"),
            @ApiResponse(responseCode = "409", description = "중복된 아이디 또는 이메일"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@RequestBody @Valid SignupRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호를 기입하여 로그인 합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호 불일치"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(
            @RequestBody @Valid LoginRequestDto request,
            HttpServletRequest httpRequest
    ) {
        String clientIp = ClientIpResolver.resolve(httpRequest);
        return ResponseEntity.ok(authService.login(request, clientIp));
    }

    @Operation(summary = "재로그인", description = "로그인 상태에서 refresh토큰을 사용하여 재로그인 합니다")
    @PostMapping("/reissue")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "유효하지 않거나 만료된 refresh token"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<TokenResponseDto> reissue(@RequestBody @Valid ReissueRequestDto request) {
        return ResponseEntity.ok(authService.reissue(request.getRefreshToken()));
    }

    @Operation(
            summary = "웹소켓 인증 티켓 발급",
            description = "기존 accessToken을 검증한 뒤, 웹소켓 연결 전용으로만 쓸 수 있는 짧은 만료의 티켓을 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "웹소켓 티켓 발급 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않거나 만료된 accessToken")
    })
    @PostMapping("/ws-ticket")
    public ResponseEntity<WsTicketResponseDto> issueWsTicket(
            @RequestHeader("Authorization") String authHeader
    ) {
        return ResponseEntity.ok(authService.issueWsTicket(authHeader));
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 진행하면서 refresh token을 삭제합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "유효하지 않거나 만료된 refresh token"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody @Valid LogoutRequestDto request,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ") && authHeader.length() > 7) {
            accessToken = authHeader.substring(7);
        }
        authService.logout(accessToken, request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "비밀번호 재설정 인증 코드 요청",
            description = "가입된 이메일로 비밀번호 재설정 인증 코드를 발송합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 재설정 인증 코드 요청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "비활성 회원"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "429", description = "이메일 인증 코드 재요청 제한"),
            @ApiResponse(responseCode = "500", description = "이메일 발송 실패 또는 서버 오류")
    })
    @PostMapping("/password-reset/request")
    public ResponseEntity<PasswordResetRequestResponseDto> requestPasswordReset(
            @RequestBody @Valid PasswordResetRequestDto request
    ) {
        return ResponseEntity.ok(authService.requestPasswordReset(request));
    }

    @Operation(
            summary = "비밀번호 재설정",
            description = "이메일 인증 코드를 확인한 뒤 새 비밀번호로 변경하고 기존 refresh token을 무효화합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 재설정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청, 만료된 인증 코드, 일치하지 않는 인증 코드 또는 기존 비밀번호와 동일"),
            @ApiResponse(responseCode = "403", description = "비활성 회원"),
            @ApiResponse(responseCode = "404", description = "이메일 인증 요청 또는 사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/password-reset/confirm")
    public ResponseEntity<PasswordResetConfirmResponseDto> resetPassword(
            @RequestBody @Valid PasswordResetConfirmRequestDto request
    ) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    @Operation(
            summary = "아이디 찾기 인증 코드 요청",
            description = "이름과 가입된 이메일이 일치하면 이메일로 인증 코드를 발송합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아이디 찾기 인증 코드 요청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "비활성 회원"),
            @ApiResponse(responseCode = "404", description = "이름과 이메일이 일치하는 회원을 찾을 수 없음"),
            @ApiResponse(responseCode = "429", description = "이메일 인증 코드 재요청 제한"),
            @ApiResponse(responseCode = "500", description = "이메일 발송 실패 또는 서버 오류")
    })
    @PostMapping("/find-id/request")
    public ResponseEntity<FindLoginIdRequestResponseDto> requestFindLoginId(
            @RequestBody @Valid FindLoginIdRequestDto request
    ) {
        return ResponseEntity.ok(authService.requestFindLoginId(request));
    }

    @Operation(
            summary = "아이디 찾기",
            description = "이메일 인증 코드를 확인한 뒤 이름·이메일이 일치하는 회원의 아이디를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아이디 찾기 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청, 만료된 인증 코드 또는 일치하지 않는 인증 코드"),
            @ApiResponse(responseCode = "403", description = "비활성 회원"),
            @ApiResponse(responseCode = "404", description = "이메일 인증 요청 또는 이름·이메일이 일치하는 회원을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/find-id/confirm")
    public ResponseEntity<FindLoginIdConfirmResponseDto> findLoginId(
            @RequestBody @Valid FindLoginIdConfirmRequestDto request
    ) {
        return ResponseEntity.ok(authService.findLoginId(request));
    }
}
