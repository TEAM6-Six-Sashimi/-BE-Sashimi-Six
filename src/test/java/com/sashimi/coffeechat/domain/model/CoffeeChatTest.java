package com.sashimi.coffeechat.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class CoffeeChatTest {

    private static final Long STUDENT_ID = 1L;
    private static final Long INSTRUCTOR_ID = 2L;
    private static final Long COURSE_ID = 10L;

    @Test
    void 커피챗_생성시_PENDING_상태로_시작한다() {
        CoffeeChat coffeeChat = CoffeeChat.create(STUDENT_ID, INSTRUCTOR_ID, COURSE_ID);

        assertThat(coffeeChat.getStudentId()).isEqualTo(STUDENT_ID);
        assertThat(coffeeChat.getInstructorId()).isEqualTo(INSTRUCTOR_ID);
        assertThat(coffeeChat.getCourseId()).isEqualTo(COURSE_ID);
        assertThat(coffeeChat.getStatus()).isEqualTo(CoffeeChatStatus.PENDING);
        assertThat(coffeeChat.getCreatedAt()).isNotNull();
        assertThat(coffeeChat.getAcceptedAt()).isNull();
        assertThat(coffeeChat.getLeftAt()).isNull();
    }

    @Test
    void PENDING_상태에서_수락하면_ACCEPTED_상태가_되고_acceptedAt이_기록된다() {
        CoffeeChat coffeeChat = CoffeeChat.create(STUDENT_ID, INSTRUCTOR_ID, COURSE_ID);

        coffeeChat.accept();

        assertThat(coffeeChat.getStatus()).isEqualTo(CoffeeChatStatus.ACCEPTED);
        assertThat(coffeeChat.getAcceptedAt()).isNotNull();
    }

    @Test
    void PENDING이_아니면_수락할_수_없다() {
        CoffeeChat coffeeChat = alreadyAccepted();

        BusinessException exception = catchThrowableOfType(
                coffeeChat::accept,
                BusinessException.class
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COFFEE_CHAT_INVALID_STATUS);
    }

    @Test
    void ACCEPTED_상태에서_나가면_LEFT_상태가_되고_leftAt이_기록된다() {
        CoffeeChat coffeeChat = alreadyAccepted();

        coffeeChat.leave();

        assertThat(coffeeChat.getStatus()).isEqualTo(CoffeeChatStatus.LEFT);
        assertThat(coffeeChat.getLeftAt()).isNotNull();
    }

    @Test
    void ACCEPTED가_아니면_나갈_수_없다() {
        CoffeeChat coffeeChat = CoffeeChat.create(STUDENT_ID, INSTRUCTOR_ID, COURSE_ID);

        BusinessException exception = catchThrowableOfType(
                coffeeChat::leave,
                BusinessException.class
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COFFEE_CHAT_INVALID_STATUS);
    }

    @Test
    void PENDING_상태는_거절_가능하다() {
        CoffeeChat coffeeChat = CoffeeChat.create(STUDENT_ID, INSTRUCTOR_ID, COURSE_ID);

        coffeeChat.validateCanReject();
    }

    @Test
    void PENDING이_아니면_거절할_수_없다() {
        CoffeeChat coffeeChat = alreadyAccepted();

        BusinessException exception = catchThrowableOfType(
                coffeeChat::validateCanReject,
                BusinessException.class
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COFFEE_CHAT_INVALID_STATUS);
    }

    @Test
    void ACCEPTED_상태에서_학생이_메시지를_보내면_메시지가_생성된다() {
        CoffeeChat coffeeChat = alreadyAccepted();

        CoffeeChatMessage message = coffeeChat.sendMessage(STUDENT_ID, "안녕하세요");

        assertThat(message.getCoffeeChatId()).isEqualTo(coffeeChat.getId());
        assertThat(message.getSenderId()).isEqualTo(STUDENT_ID);
        assertThat(message.getContent()).isEqualTo("안녕하세요");
        assertThat(message.isRead()).isFalse();
    }

    @Test
    void ACCEPTED_상태에서_강사가_메시지를_보내면_메시지가_생성된다() {
        CoffeeChat coffeeChat = alreadyAccepted();

        CoffeeChatMessage message = coffeeChat.sendMessage(INSTRUCTOR_ID, "네 안녕하세요");

        assertThat(message.getSenderId()).isEqualTo(INSTRUCTOR_ID);
    }

    @Test
    void ACCEPTED가_아니면_메시지를_보낼_수_없다() {
        CoffeeChat coffeeChat = CoffeeChat.create(STUDENT_ID, INSTRUCTOR_ID, COURSE_ID);

        BusinessException exception = catchThrowableOfType(
                () -> coffeeChat.sendMessage(STUDENT_ID, "메시지"),
                BusinessException.class
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COFFEE_CHAT_INVALID_STATUS);
    }

    @Test
    void 참여자가_아니면_메시지를_보낼_수_없다() {
        CoffeeChat coffeeChat = alreadyAccepted();
        Long strangerId = 999L;

        BusinessException exception = catchThrowableOfType(
                () -> coffeeChat.sendMessage(strangerId, "메시지"),
                BusinessException.class
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COFFEE_CHAT_MESSAGE_FORBIDDEN);
    }

    @Test
    void senderId가_null이면_forbidden_예외가_발생한다() {
        CoffeeChat coffeeChat = alreadyAccepted();

        BusinessException exception = catchThrowableOfType(
                () -> coffeeChat.sendMessage(null, "메시지"),
                BusinessException.class
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COFFEE_CHAT_MESSAGE_FORBIDDEN);
    }

    private CoffeeChat alreadyAccepted() {
        CoffeeChat coffeeChat = CoffeeChat.create(STUDENT_ID, INSTRUCTOR_ID, COURSE_ID);
        coffeeChat.accept();
        return coffeeChat;
    }
}
