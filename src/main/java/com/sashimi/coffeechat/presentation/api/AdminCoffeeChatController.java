package com.sashimi.coffeechat.presentation.api;

import com.sashimi.coffeechat.application.usecase.CoffeeChatCommandUseCase;
import com.sashimi.coffeechat.presentation.api.response.CoffeeChatBackfillResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자 커피챗", description = "커피챗 관리자 전용 API (ROLE_ADMIN 전용)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/coffee-chats")
public class AdminCoffeeChatController {

    private final CoffeeChatCommandUseCase coffeeChatCommandUseCase;

    @Operation(
            summary = "기존 수강생 채팅방 백필",
            description = "커피챗 자동생성 기능 배포 이전에 결제 완료된 수강신청 중 채팅방이 없는 건을 찾아 생성한다. "
                    + "이미 방이 있는 건은 건너뛰므로 여러 번 호출해도 안전하다."
    )
    @PostMapping("/backfill")
    public ResponseEntity<CoffeeChatBackfillResponse> backfill() {
        int createdCount = coffeeChatCommandUseCase.backfillMissingChatRooms();
        return ResponseEntity.ok(new CoffeeChatBackfillResponse(createdCount));
    }
}
