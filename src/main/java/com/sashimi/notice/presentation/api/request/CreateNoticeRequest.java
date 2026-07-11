package com.sashimi.notice.presentation.api.request;

import com.sashimi.notice.application.command.CreateNoticeCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateNoticeRequest(

        @Schema(description = "공지사항 제목", example = "[필독] 2026년 하반기 서비스 이용약관 변경 안내")
        @NotBlank(message = "공지사항 제목을 입력해 주세요.")
        @Size(max = 200, message = "공지사항 제목은 200자 이하로 입력해 주세요.")
        String title,

        @Schema(description = "공지사항 내용", example = "안녕하세요. 핏격입니다. 서비스 이용약관이 변경될 예정입니다.")
        @NotBlank(message = "공지사항 내용을 입력해 주세요.")
        String content,

        @Schema(description = "고정 여부", example = "true")
        Boolean pinned
) {

    public CreateNoticeCommand toCommand() {
        return new CreateNoticeCommand(
                title,
                content,
                Boolean.TRUE.equals(pinned)
        );
    }
}