package com.sashimi.auth.controller;

import com.sashimi.auth.dto.LoginRequestDto;
import com.sashimi.auth.dto.LogoutRequestDto;
import com.sashimi.auth.dto.ReissueRequestDto;
import com.sashimi.auth.dto.TokenResponseDto;
import com.sashimi.auth.service.AuthService;
import com.sashimi.user.dto.LoginIdCheckResponseDto;
import com.sashimi.user.dto.SignupRequestDto;
import com.sashimi.user.dto.UserResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login-id/check")
    public ResponseEntity<LoginIdCheckResponseDto> checkLoginId(@RequestParam String loginId) {
        return ResponseEntity.ok(authService.checkLoginId(loginId));
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@RequestBody @Valid SignupRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody @Valid LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDto> reissue(@RequestBody @Valid ReissueRequestDto request) {
        return ResponseEntity.ok(authService.reissue(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid LogoutRequestDto request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}
