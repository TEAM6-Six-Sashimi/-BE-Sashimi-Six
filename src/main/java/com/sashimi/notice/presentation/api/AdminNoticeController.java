package com.sashimi.notice.presentation.api;

import com.sashimi.notice.application.usecase.NoticeCommandUseCase;
import com.sashimi.notice.application.usecase.NoticeQueryUseCase;
import com.sashimi.notice.presentation.api.request.CreateNoticeRequest;
import com.sashimi.notice.presentation.api.response.NoticeDetailResponse;
import com.sashimi.notice.presentation.api.response.NoticeListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자 공지사항", description = "관리자 공지사항 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/notices")
public class AdminNoticeController {

    private final NoticeQueryUseCase noticeQueryUseCase;
    private final NoticeCommandUseCase noticeCommandUseCase;

    @Operation(
            summary = "관리자 공지사항 목록 조회",
            description = "관리자가 등록된 공지사항 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<NoticeListResponse> getNotices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                NoticeListResponse.from(
                        noticeQueryUseCase.getNotices(page, size)
                )
        );
    }

    @Operation(
            summary = "관리자 공지사항 상세 조회",
            description = "관리자가 공지사항 상세 내용을 조회합니다."
    )
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeDetailResponse> getNotice(
            @PathVariable Long noticeId
    ) {
        return ResponseEntity.ok(
                NoticeDetailResponse.from(
                        noticeQueryUseCase.getNotice(noticeId)
                )
        );
    }

    @Operation(
            summary = "공지사항 등록",
            description = "관리자가 공지사항을 등록합니다. 고정 여부는 등록 시 한 번만 선택합니다."
    )
    @PostMapping
    public ResponseEntity<NoticeDetailResponse> createNotice(
            @Valid @RequestBody CreateNoticeRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        NoticeDetailResponse.from(
                                noticeCommandUseCase.createNotice(request.toCommand())
                        )
                );
    }

    @Operation(
            summary = "공지사항 삭제",
            description = "관리자가 공지사항을 물리 삭제합니다."
    )
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(
            @PathVariable Long noticeId
    ) {
        noticeCommandUseCase.deleteNotice(noticeId);
        return ResponseEntity.noContent().build();
    }
}