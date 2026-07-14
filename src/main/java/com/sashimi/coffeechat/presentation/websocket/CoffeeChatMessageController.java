package com.sashimi.coffeechat.presentation.websocket;

import com.sashimi.coffeechat.application.result.CoffeeChatMessageResult;
import com.sashimi.coffeechat.application.usecase.CoffeeChatCommandUseCase;
import com.sashimi.coffeechat.presentation.api.request.SendCoffeeChatMessageRequest;
import com.sashimi.coffeechat.presentation.api.response.CoffeeChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class CoffeeChatMessageController {

    private static final String USER_QUEUE_DESTINATION = "/queue/coffee-chats/";

    private final CoffeeChatCommandUseCase coffeeChatCommandUseCase;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/coffee-chats/{chatId}/messages")
    public void sendMessage(
            @DestinationVariable Long chatId,
            @Payload SendCoffeeChatMessageRequest request,
            Principal principal
    ) {
        Long senderId = Long.valueOf(principal.getName());

        CoffeeChatMessageResult result = coffeeChatCommandUseCase.sendMessage(chatId, senderId, request.content());
        CoffeeChatMessageResponse response = CoffeeChatMessageResponse.from(result.message());

        messagingTemplate.convertAndSendToUser(
                String.valueOf(result.studentId()), USER_QUEUE_DESTINATION + chatId, response);
        messagingTemplate.convertAndSendToUser(
                String.valueOf(result.instructorId()), USER_QUEUE_DESTINATION + chatId, response);
    }
}
