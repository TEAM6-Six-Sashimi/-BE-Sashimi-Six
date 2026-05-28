package com.sashimi.resume.presentation.api.request;

import com.sashimi.resume.application.command.UpdateResumeCommand;
import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateResumeRequest(

        @Schema(description = "이력서 제목", example = "수정된 이력서 제목")
        String title,

        @Schema(
                description = "이력서 내용 JSON",
                example = "{\"basic\":{\"name\":\"박학생\",\"email\":\"student1@test.com\"},\"education\":[],\"career\":[],\"skills\":[\"Java\"],\"certificates\":[]}"
        )
        String content,

        @Schema(description = "기본 이력서 여부", example = "false")
        Boolean defaultResume
) {

    public UpdateResumeCommand toCommand(Long userId, Long resumeId) {
        return new UpdateResumeCommand(
                userId,
                resumeId,
                title,
                content,
                defaultResume
        );
    }
}