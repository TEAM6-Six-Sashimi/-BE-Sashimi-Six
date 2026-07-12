package com.sashimi.coffeechat.presentation.api;

import com.sashimi.coffeechat.application.usecase.CoffeeChatCommandUseCase;
import com.sashimi.coffeechat.application.usecase.CoffeeChatQueryUseCase;
import com.sashimi.coffeechat.presentation.api.response.CoffeeChatMessageResponse;
import com.sashimi.coffeechat.presentation.api.response.CoffeeChatSummaryResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/student/coffee-chats")
@RequiredArgsConstructor
public class StudentCoffeeChatController {

    private final CoffeeChatQueryUseCase coffeeChatQueryUseCase;
    private final CoffeeChatCommandUseCase coffeeChatCommandUseCase;

    @GetMapping
    public ResponseEntity<List<CoffeeChatSummaryResponse>> getMyChats(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        List<CoffeeChatSummaryResponse> response = coffeeChatQueryUseCase.getStudentChats(principal.getId())
                .stream()
                .map(CoffeeChatSummaryResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<CoffeeChatMessageResponse>> getMessages(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<CoffeeChatMessageResponse> response = coffeeChatQueryUseCase
                .getMessages(chatId, principal.getId(), page, size)
                .stream()
                .map(CoffeeChatMessageResponse::from)
                .toList();
        coffeeChatCommandUseCase.markMessagesAsRead(chatId, principal.getId());
        return ResponseEntity.ok(response);
    }
}
