package com.sashimi.chatbot.application.service;

import com.sashimi.chatbot.application.port.CareerChatPort;
import com.sashimi.chatbot.domain.model.ChatMessage;
import com.sashimi.global.ratelimit.RateLimiterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("ChatbotService — 사용량 제한(IP 주간 호출)")
class ChatbotServiceTest {

    private static final int WEEKLY_LIMIT = 10;
    private static final String IP = "1.2.3.4";

    private final CareerChatPort careerChatPort = mock(CareerChatPort.class);
    private final RateLimiterService rateLimiterService = mock(RateLimiterService.class);
    private final ChatbotService service =
            new ChatbotService(careerChatPort, rateLimiterService, WEEKLY_LIMIT);

    @Test
    @DisplayName("한도 내면 AI 답변을 반환하고, IP·주간 윈도우로 제한을 검사한다")
    void 한도_내_정상_답변() {
        when(rateLimiterService.isAllowed(anyString(), anyInt(), anyInt())).thenReturn(true);
        when(careerChatPort.generateReply(anyString(), any())).thenReturn("정상 답변");

        String reply = service.sendMessage("안녕", List.of(), IP);

        assertThat(reply).isEqualTo("정상 답변");
        // IP가 키에 들어가고, 주간 윈도우(7일=604800초)로 검사하는지 확인
        verify(rateLimiterService).isAllowed(
                eq("chatbot:ip:" + IP), eq(WEEKLY_LIMIT), eq(7 * 24 * 60 * 60));
    }

    @Test
    @DisplayName("한도 초과면 AI를 호출하지 않고 안내 문구를 200으로 반환한다")
    void 한도_초과_안내문구() {
        when(rateLimiterService.isAllowed(anyString(), anyInt(), anyInt())).thenReturn(false);

        String reply = service.sendMessage("안녕", List.of(), IP);

        assertThat(reply).contains("이번 주 무료 상담 횟수");
        verify(careerChatPort, never()).generateReply(anyString(), any()); // 비용 절감 핵심: AI 미호출
    }

    @Test
    @DisplayName("빈 질문은 제한 검사 전에 400으로 막고, 사용량을 소모하지 않는다")
    void 빈_질문_400_미차감() {
        assertThatThrownBy(() -> service.sendMessage("  ", List.of(), IP))
                .isInstanceOf(ResponseStatusException.class);

        verify(rateLimiterService, never()).isAllowed(anyString(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("IP를 못 구하면(null) 제한하지 않고 정상 처리한다(정상 사용자 보호)")
    void IP_없으면_제한_안함() {
        when(careerChatPort.generateReply(anyString(), any())).thenReturn("정상 답변");

        String reply = service.sendMessage("안녕", List.of(), null);

        assertThat(reply).isEqualTo("정상 답변");
        verify(rateLimiterService, never()).isAllowed(anyString(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("AI 호출이 실패해도 안내 문구를 200으로 반환한다(기존 계약 유지)")
    void AI_실패_폴백() {
        when(rateLimiterService.isAllowed(anyString(), anyInt(), anyInt())).thenReturn(true);
        when(careerChatPort.generateReply(anyString(), any()))
                .thenThrow(new RuntimeException("gemini down"));

        String reply = service.sendMessage("안녕", List.of(), IP);

        assertThat(reply).contains("죄송해요");
    }
}
