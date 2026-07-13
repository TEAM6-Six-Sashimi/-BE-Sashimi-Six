package com.sashimi.coffeechat.presentation.api;

import com.sashimi.coffeechat.application.usecase.CoffeeChatCommandUseCase;
import com.sashimi.coffeechat.application.usecase.CoffeeChatQueryUseCase;
import com.sashimi.coffeechat.presentation.api.response.CoffeeChatMessageResponse;
import com.sashimi.coffeechat.presentation.api.response.InstructorCoffeeChatSummaryResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/instructor/coffee-chats")
@RequiredArgsConstructor
public class InstructorCoffeeChatController {

    private final CoffeeChatQueryUseCase coffeeChatQueryUseCase;
    private final CoffeeChatCommandUseCase coffeeChatCommandUseCase;

    @GetMapping("/pending")
    public ResponseEntity<List<InstructorCoffeeChatSummaryResponse>> getPendingChats(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        List<InstructorCoffeeChatSummaryResponse> response = coffeeChatQueryUseCase
                .getInstructorPendingChats(principal.getId())
                .stream()
                .map(InstructorCoffeeChatSummaryResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<InstructorCoffeeChatSummaryResponse>> getActiveChats(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        List<InstructorCoffeeChatSummaryResponse> response = coffeeChatQueryUseCase
                .getInstructorActiveChats(principal.getId())
                .stream()
                .map(InstructorCoffeeChatSummaryResponse::from)
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

        try {
            coffeeChatCommandUseCase.markMessagesAsRead(chatId, principal.getId());
        } catch (RuntimeException e) {
            log.warn("메시지 읽음처리 실패 - chatId={}, readerId={}", chatId, principal.getId(), e);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{chatId}/accept")
    public ResponseEntity<Void> accept(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long chatId) {
        coffeeChatCommandUseCase.accept(chatId, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{chatId}/reject")
    public ResponseEntity<Void> reject(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long chatId) {
        coffeeChatCommandUseCase.reject(chatId, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{chatId}/leave")
    public ResponseEntity<Void> leave(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long chatId) {
        coffeeChatCommandUseCase.leave(chatId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
