package com.sashimi.coffeechat.integration;

import com.sashimi.certificate.infrastructure.CodefTokenManager;
import com.sashimi.coffeechat.domain.model.CoffeeChat;
import com.sashimi.coffeechat.domain.repository.CoffeeChatRepository;
import com.sashimi.global.storage.FileStoragePort;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzePort;
import com.sashimi.security.blacklist.TokenBlacklistService;
import com.sashimi.security.jwt.JwtTokenProvider;
import com.sashimi.security.session.TokenVersionService;
import com.sashimi.verification.application.port.EmailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "gemini"})
class CoffeeChatWebSocketIntegrationTest {

    private static final AtomicLong ID_SEQUENCE = new AtomicLong(910_000_000L);

    @LocalServerPort
    private int port;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CoffeeChatRepository coffeeChatRepository;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @MockitoBean
    private TokenVersionService tokenVersionService;

    @MockitoBean
    private EmailSender emailSender;

    @MockitoBean
    private JobPostingRecommendationAnalyzePort jobPostingRecommendationAnalyzePort;

    @MockitoBean
    private CodefTokenManager codefTokenManager;

    @MockitoBean
    private FileStoragePort fileStoragePort;

    private WebSocketStompClient stompClient;

    @BeforeEach
    void setUp() {
        when(tokenVersionService.isValidVersion(anyLong(), anyLong())).thenReturn(true);

        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());
    }

    @Test
    void 학생이_보낸_메시지를_강사가_실시간으로_수신한다() throws Exception {
        Long studentId = ID_SEQUENCE.incrementAndGet();
        Long instructorId = ID_SEQUENCE.incrementAndGet();
        Long chatId = createCoffeeChat(studentId, instructorId);

        StompSession instructorSession = connect(instructorId);
        BlockingQueue<Map<String, Object>> received = new LinkedBlockingQueue<>();
        instructorSession.subscribe("/user/queue/coffee-chats/" + chatId, new MapPayloadFrameHandler(received));

        StompSession studentSession = connect(studentId);
        studentSession.send("/app/coffee-chats/" + chatId + "/messages", Map.of("content", "안녕하세요"));

        Map<String, Object> message = received.poll(5, TimeUnit.SECONDS);

        assertThat(message).isNotNull();
        assertThat(message.get("content")).isEqualTo("안녕하세요");
        assertThat(((Number) message.get("senderId")).longValue()).isEqualTo(studentId);

        studentSession.disconnect();
        instructorSession.disconnect();
    }

    @Test
    void 유효하지_않은_토큰으로는_연결이_거부된다() {
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer invalid-token");

        CompletableFuture<StompSession> future = stompClient.connectAsync(
                wsUrl(), new WebSocketHttpHeaders(), connectHeaders, new StompSessionHandlerAdapter() {});

        assertThatThrownBy(() -> future.get(5, TimeUnit.SECONDS))
                .isInstanceOf(ExecutionException.class);
    }

    private StompSession connect(Long userId) throws Exception {
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + issueToken(userId));

        return stompClient.connectAsync(wsUrl(), new WebSocketHttpHeaders(), connectHeaders,
                        new StompSessionHandlerAdapter() {})
                .get(5, TimeUnit.SECONDS);
    }

    private String issueToken(Long userId) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                String.valueOf(userId), null, List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));
        return jwtTokenProvider.generateToken(authentication, userId, 1L).getAccessToken();
    }

    private String wsUrl() {
        return "ws://localhost:" + port + "/ws-coffeechat";
    }

    private Long createCoffeeChat(Long studentId, Long instructorId) {
        CoffeeChat coffeeChat = CoffeeChat.create(studentId, instructorId, ID_SEQUENCE.incrementAndGet());
        return coffeeChatRepository.save(coffeeChat).getId();
    }

    private static class MapPayloadFrameHandler extends StompSessionHandlerAdapter implements StompFrameHandler {

        private final BlockingQueue<Map<String, Object>> queue;

        MapPayloadFrameHandler(BlockingQueue<Map<String, Object>> queue) {
            this.queue = queue;
        }

        @Override
        public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
            return Map.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            queue.add((Map<String, Object>) payload);
        }
    }
}
