package com.sashimi.chatbot.application.service;

import com.sashimi.chatbot.application.port.CareerChatPort;
import com.sashimi.chatbot.application.usecase.ChatbotUseCase;
import com.sashimi.chatbot.domain.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 챗봇 유스케이스 구현(애플리케이션 뼈대).
 *
 * <p>요청 검증 후 AI 포트로 위임한다.
 * AI 호출이 실패해도 사용자에게는 자연스러운 안내 문구를 반환한다(계약: 에러도 200 + reply).
 */
@Slf4j
@Service
public class ChatbotService implements ChatbotUseCase {

    /** 현재 질문 최대 길이. */
    private static final int MAX_MESSAGE_LENGTH = 2000;
    /** 함께 보낼 수 있는 이전 대화 최대 개수(초과 시 최근 것만 유지). */
    private static final int MAX_HISTORY = 20;
    /** AI 호출 실패 시 사용자에게 보여줄 안내. */
    private static final String FALLBACK_REPLY =
            "죄송해요, 지금은 답변을 드리기 어려워요. 잠시 후 다시 시도해 주세요.";

    private final CareerChatPort careerChatPort;

    public ChatbotService(CareerChatPort careerChatPort) {
        this.careerChatPort = careerChatPort;
    }

    @Override
    public String sendMessage(String message, List<ChatMessage> history) {
        validateMessage(message);
        List<ChatMessage> recentHistory = trimHistory(history);

        try {
            return careerChatPort.generateReply(message, recentHistory);
        } catch (Exception e) {
            // AI 호출 실패는 사용자 잘못이 아니므로, 에러 대신 안내 문구를 200으로 반환한다.
            log.error("[Chatbot] 답변 생성 실패 - messageLength={}", message.length(), e);
            return FALLBACK_REPLY;
        }
    }

    private void validateMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "질문을 입력해 주세요.");
        }
        if (message.length() > MAX_MESSAGE_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "질문이 너무 깁니다.");
        }
    }

    /** 이전 대화가 너무 길면 최근 MAX_HISTORY개만 남긴다(토큰·비용 방어). */
    private List<ChatMessage> trimHistory(List<ChatMessage> history) {
        if (history == null || history.isEmpty()) {
            return List.of();
        }
        if (history.size() <= MAX_HISTORY) {
            return history;
        }
        return history.subList(history.size() - MAX_HISTORY, history.size());
    }
}
