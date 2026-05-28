package com.sashimi.resume.presentation.api.response;

import com.sashimi.resume.domain.model.Resume;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "이력서 응답")
public record ResumeResponse(

        @Schema(description = "이력서 ID", example = "1")
        Long resumeId,

        @Schema(description = "이력서 제목", example = "백엔드 신입 개발자 이력서")
        String title,

        @Schema(
                description = "이력서 내용 JSON",
                example = "{\"basic\":{\"name\":\"박학생\",\"email\":\"student1@test.com\",\"phone\":\"010-1234-5678\"},\"education\":[],\"career\":[],\"skills\":[\"Java\",\"Spring Boot\"],\"certificates\":[]}"
        )
        String content,

        @Schema(description = "기본 이력서 여부", example = "true")
        boolean defaultResume,

        @Schema(description = "생성일시", example = "2026-05-27T16:30:00")
        LocalDateTime createdAt,

        @Schema(description = "수정일시", example = "2026-05-27T16:40:00")
        LocalDateTime updatedAt
) {
    public static ResumeResponse from(Resume resume) {
        return new ResumeResponse(
                resume.resumeId(),
                resume.title(),
                resume.content(),
                resume.defaultResume(),
                resume.createdAt(),
                resume.updatedAt()
        );
    }
}