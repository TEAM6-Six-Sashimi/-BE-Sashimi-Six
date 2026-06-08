package com.sashimi.resume.presentation.api.request;

import com.sashimi.resume.application.command.CreateResumeCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateResumeRequest(

        @Schema(description = "이력서 제목", example = "백엔드 신입 개발자 이력서")
        @NotBlank
        String title,

        @Schema(
                description = "이력서 내용 JSON",
                example = "{\"basic\":{\"name\":\"박학생\",\"email\":\"student1@test.com\",\"phone\":\"010-1234-5678\"},\"education\":[],\"career\":[],\"skills\":[\"Java\",\"Spring Boot\"],\"certificates\":[]}"
        )
        @NotBlank
        String content,

        @Schema(description = "기본 이력서 여부", example = "true")
        boolean defaultResume
) {

    public CreateResumeCommand toCommand(Long userId) {
        return new CreateResumeCommand(
                userId,
                title,
                content,
                defaultResume
        );
    }
}