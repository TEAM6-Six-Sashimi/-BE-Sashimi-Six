package com.sashimi.chatbot.presentation.api;

import com.sashimi.chatbot.application.usecase.ChatbotUseCase;
import com.sashimi.chatbot.presentation.api.request.ChatMessageRequest;
import com.sashimi.chatbot.presentation.api.response.ChatReplyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 진로상담 챗봇(핏봇) API. 비로그인 포함 누구나 사용할 수 있다.
 * 대화는 저장하지 않으며, 매 요청마다 이전 대화 전체를 받아 처리한다(무상태).
 */
@Tag(name = "챗봇 API", description = "AI 진로상담 챗봇(핏봇) API")
@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotUseCase chatbotUseCase;

    public ChatbotController(ChatbotUseCase chatbotUseCase) {
        this.chatbotUseCase = chatbotUseCase;
    }

    @Operation(
            summary = "챗봇 대화",
            description = "현재 질문과 이전 대화(history)를 보내면 AI가 답변을 생성해 반환합니다. 대화는 서버에 저장되지 않습니다."
    )
    @PostMapping("/messages")
    public ResponseEntity<ChatReplyResponse> sendMessage(@RequestBody ChatMessageRequest request) {
        String reply = chatbotUseCase.sendMessage(request.message(), request.toHistory());
        return ResponseEntity.ok(new ChatReplyResponse(reply));
    }
}
