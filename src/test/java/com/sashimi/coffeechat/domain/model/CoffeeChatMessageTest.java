package com.sashimi.coffeechat.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class CoffeeChatMessageTest {

    private static final Long COFFEE_CHAT_ID = 1L;
    private static final Long SENDER_ID = 10L;

    @Test
    void 메시지_생성시_읽음여부는_false이고_생성시각이_기록된다() {
        CoffeeChatMessage message = CoffeeChatMessage.create(COFFEE_CHAT_ID, SENDER_ID, "안녕하세요");

        assertThat(message.getCoffeeChatId()).isEqualTo(COFFEE_CHAT_ID);
        assertThat(message.getSenderId()).isEqualTo(SENDER_ID);
        assertThat(message.getContent()).isEqualTo("안녕하세요");
        assertThat(message.getMessageType()).isEqualTo(CoffeeChatMessageType.TEXT);
        assertThat(message.isRead()).isFalse();
        assertThat(message.getCreatedAt()).isNotNull();
    }

    @Test
    void createSystemMessage로_생성하면_읽음처리된_시스템메시지가_생성된다() {
        CoffeeChatMessage message = CoffeeChatMessage.createSystemMessage(
                COFFEE_CHAT_ID, SENDER_ID, CoffeeChatMessageType.SYSTEM_LEAVE);

        assertThat(message.getCoffeeChatId()).isEqualTo(COFFEE_CHAT_ID);
        assertThat(message.getSenderId()).isEqualTo(SENDER_ID);
        assertThat(message.getMessageType()).isEqualTo(CoffeeChatMessageType.SYSTEM_LEAVE);
        assertThat(message.getContent()).isNotBlank();
        assertThat(message.isRead()).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void content가_비어있으면_생성할_수_없다(String invalidContent) {
        BusinessException exception = catchThrowableOfType(
                () -> CoffeeChatMessage.create(COFFEE_CHAT_ID, SENDER_ID, invalidContent),
                BusinessException.class
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT_VALUE);
    }

    @Test
    void markAsRead_호출하면_읽음_상태가_된다() {
        CoffeeChatMessage message = CoffeeChatMessage.create(COFFEE_CHAT_ID, SENDER_ID, "안녕하세요");

        message.markAsRead();

        assertThat(message.isRead()).isTrue();
    }
}
