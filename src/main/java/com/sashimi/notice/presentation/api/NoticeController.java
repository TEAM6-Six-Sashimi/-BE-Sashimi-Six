package com.sashimi.notice.presentation.api;

import com.sashimi.notice.application.usecase.NoticeQueryUseCase;
import com.sashimi.notice.presentation.api.response.NoticeDetailResponse;
import com.sashimi.notice.presentation.api.response.NoticeListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "공지사항", description = "공지사항 공개 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeQueryUseCase noticeQueryUseCase;

    @Operation(
            summary = "공지사항 목록 조회",
            description = "메인 페이지 공지사항 영역과 공지사항 전체 목록에서 공통으로 사용합니다. size는 최대 500까지 허용됩니다."
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
            summary = "공지사항 상세 조회",
            description = "공지사항 목록에서 선택한 공지사항의 상세 내용을 조회합니다."
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
}