package com.sashimi.coffeechat.application.service;

import com.sashimi.coffeechat.application.command.ApplyCoffeeChatCommand;
import com.sashimi.coffeechat.application.usecase.CoffeeChatCommandUseCase;
import com.sashimi.coffeechat.domain.model.CoffeeChat;
import com.sashimi.coffeechat.domain.model.CoffeeChatMessage;
import com.sashimi.coffeechat.domain.model.CoffeeChatStatus;
import com.sashimi.coffeechat.domain.repository.CoffeeChatMessageRepository;
import com.sashimi.coffeechat.domain.repository.CoffeeChatRepository;
import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.repository.CourseRepository;
import com.sashimi.enrollment.application.port.EnrollmentPort;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CoffeeChatCommandService implements CoffeeChatCommandUseCase {

    private final CoffeeChatRepository coffeeChatRepository;
    private final CoffeeChatMessageRepository coffeeChatMessageRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentPort enrollmentPort;

    @Override
    public void apply(ApplyCoffeeChatCommand command) {
        Course course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        if (!course.getInstructorId().equals(command.instructorId())) {
            throw new BusinessException(ErrorCode.COURSE_FORBIDDEN);
        }

        if (!enrollmentPort.isEnrolled(command.studentId(), command.courseId())) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_NOT_ENROLLED);
        }

        if (coffeeChatRepository.existsByStudentIdAndInstructorIdAndCourseIdAndStatusIn(
                command.studentId(), command.instructorId(), command.courseId(),
                List.of(CoffeeChatStatus.PENDING, CoffeeChatStatus.ACCEPTED))) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_ALREADY_EXISTS);
        }

        coffeeChatRepository.save(
                CoffeeChat.create(command.studentId(), command.instructorId(), command.courseId()));
    }

    @Override
    public void accept(Long chatId, Long instructorId) {
        CoffeeChat chat = getChatForInstructor(chatId, instructorId);
        chat.accept();
        coffeeChatRepository.save(chat);
    }

    @Override
    public void reject(Long chatId, Long instructorId) {
        CoffeeChat chat = getChatForInstructor(chatId, instructorId);
        chat.validateCanReject();
        coffeeChatRepository.deleteById(chatId);
    }

    @Override
    public void leave(Long chatId, Long instructorId) {
        CoffeeChat chat = getChatForInstructor(chatId, instructorId);
        chat.leave();
        coffeeChatRepository.save(chat);
    }

    @Override
    public void sendMessage(Long chatId, Long senderId, String content) {
        CoffeeChat chat = coffeeChatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEE_CHAT_NOT_FOUND));

        CoffeeChatMessage message = chat.sendMessage(senderId, content);
        coffeeChatMessageRepository.save(message);
    }

    @Override
    public void markMessagesAsRead(Long chatId, Long readerId) {
        CoffeeChat chat = coffeeChatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEE_CHAT_NOT_FOUND));

        if (!chat.isParticipant(readerId)) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_FORBIDDEN);
        }

        coffeeChatMessageRepository.markAllAsRead(chatId, readerId);
    }

    private CoffeeChat getChatForInstructor(Long chatId, Long instructorId) {
        CoffeeChat chat = coffeeChatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEE_CHAT_NOT_FOUND));

        if (!chat.getInstructorId().equals(instructorId)) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_FORBIDDEN);
        }
        return chat;
    }
}
