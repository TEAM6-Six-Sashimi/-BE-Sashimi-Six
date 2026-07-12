package com.sashimi.coffeechat.application.service;

import com.sashimi.coffeechat.application.query.CoffeeChatSummaryView;
import com.sashimi.coffeechat.application.usecase.CoffeeChatQueryUseCase;
import com.sashimi.coffeechat.domain.model.CoffeeChat;
import com.sashimi.coffeechat.domain.model.CoffeeChatMessage;
import com.sashimi.coffeechat.domain.model.CoffeeChatStatus;
import com.sashimi.coffeechat.domain.repository.CoffeeChatMessageRepository;
import com.sashimi.coffeechat.domain.repository.CoffeeChatRepository;
import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseStatus;
import com.sashimi.course.domain.repository.CourseRepository;
import com.sashimi.enrollment.application.port.EnrollmentPort;
import com.sashimi.enrollment.application.port.EnrollmentSummary;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoffeeChatQueryService implements CoffeeChatQueryUseCase {

    private final CoffeeChatRepository coffeeChatRepository;
    private final CoffeeChatMessageRepository coffeeChatMessageRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentPort enrollmentPort;

    @Override
    public List<CoffeeChatSummaryView> getStudentChats(Long studentId) {
        List<CoffeeChat> chats = coffeeChatRepository.findAllByStudentId(studentId);
        return toSummaryViews(chats, studentId);
    }

    @Override
    public List<CoffeeChatSummaryView> getInstructorPendingChats(Long instructorId) {
        List<CoffeeChat> chats = coffeeChatRepository
                .findAllByInstructorIdAndStatusOrderByCreatedAtAsc(instructorId, CoffeeChatStatus.PENDING);
        return toSummaryViews(chats, instructorId);
    }

    @Override
    public List<CoffeeChatSummaryView> getInstructorActiveChats(Long instructorId) {
        List<CoffeeChat> chats = coffeeChatRepository
                .findAllByInstructorIdAndStatusOrderByAcceptedAtDesc(instructorId, CoffeeChatStatus.ACCEPTED);
        return toSummaryViews(chats, instructorId);
    }

    @Override
    public List<CoffeeChatMessage> getMessages(Long chatId, Long requesterId, int page, int size) {
        CoffeeChat chat = coffeeChatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEE_CHAT_NOT_FOUND));

        if (!chat.isParticipant(requesterId)) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_FORBIDDEN);
        }

        return coffeeChatMessageRepository.findAllByCoffeeChatId(chatId, page, size);
    }

    @Override
    public List<Course> getApplicableCourses(Long studentId, Long instructorId) {
        Set<Long> enrolledCourseIds = enrollmentPort.getEnrollmentsByUser(studentId)
                .stream()
                .map(EnrollmentSummary::courseId)
                .collect(Collectors.toSet());

        if (enrolledCourseIds.isEmpty()) {
            return List.of();
        }

        return courseRepository.findByInstructorIdAndStatusIn(instructorId, List.of(CourseStatus.values()))
                .stream()
                .filter(course -> enrolledCourseIds.contains(course.getId()))
                .collect(Collectors.toList());
    }

    private List<CoffeeChatSummaryView> toSummaryViews(List<CoffeeChat> chats, Long excludeSenderId) {
        List<Long> chatIds = chats.stream().map(CoffeeChat::getId).collect(Collectors.toList());
        Set<Long> unreadChatIds =
                coffeeChatMessageRepository.findCoffeeChatIdsWithUnreadMessages(chatIds, excludeSenderId);

        return chats.stream()
                .map(chat -> new CoffeeChatSummaryView(
                        chat.getId(),
                        chat.getStudentId(),
                        chat.getInstructorId(),
                        chat.getCourseId(),
                        chat.getStatus(),
                        chat.getCreatedAt(),
                        chat.getAcceptedAt(),
                        unreadChatIds.contains(chat.getId())
                ))
                .collect(Collectors.toList());
    }
}
