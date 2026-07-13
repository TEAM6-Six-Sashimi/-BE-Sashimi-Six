package com.sashimi.coverletter.presentation.api;

import com.sashimi.coverletter.application.service.CoverLetterService;
import com.sashimi.coverletter.presentation.api.request.UpdateCoverLettersRequest;
import com.sashimi.coverletter.presentation.api.response.CoverLettersResponse;
import com.sashimi.global.exception.ErrorResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "자기소개서 관리", description = "자기소개서 작성, 조회, 수정 API")
@RestController
@RequestMapping("/cover-letters")
public class CoverLetterController {

    private final CoverLetterService service;

    public CoverLetterController(
            CoverLetterService service
    ) {
        this.service = service;
    }

    @Operation(
            summary = "자기소개서 조회",
            description = "로그인 사용자의 자기소개서 문항과 작성 내용을 조회합니다. 작성 내용이 없으면 기본 문항과 빈 content를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "자기소개서 조회 성공",
                    content = @Content(schema = @Schema(implementation = CoverLettersResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public CoverLettersResponse getMyCoverLetters(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return service.getMyCoverLetters(
                principal.getId()
        );
    }

    @Operation(
            summary = "자기소개서 저장/수정",
            description = "로그인 사용자의 자기소개서 문항별 답변을 저장하거나 수정합니다. 요청에 포함된 문항만 갱신합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "자기소개서 저장/수정 성공",
                    content = @Content(schema = @Schema(implementation = CoverLettersResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 문항 또는 글자 수 초과",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping
    public CoverLettersResponse updateMyCoverLetters(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody UpdateCoverLettersRequest request
    ) {
        return service.updateMyCoverLetters(
                principal.getId(),
                request
        );
    }
}