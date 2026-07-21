package com.sashimi.coffeechat.application.service;

import com.sashimi.coffeechat.application.query.CoffeeChatSummaryView;
import com.sashimi.coffeechat.application.usecase.CoffeeChatQueryUseCase;
import com.sashimi.coffeechat.domain.model.CoffeeChat;
import com.sashimi.coffeechat.domain.model.CoffeeChatMessage;
import com.sashimi.coffeechat.domain.model.CoffeeChatStatus;
import com.sashimi.coffeechat.domain.repository.CoffeeChatMessageRepository;
import com.sashimi.coffeechat.domain.repository.CoffeeChatRepository;
import com.sashimi.course.application.port.InstructorPort;
import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.repository.CourseRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoffeeChatQueryService implements CoffeeChatQueryUseCase {

    private static final int MAX_MESSAGE_PAGE_SIZE = 200;

    private static final Comparator<CoffeeChatSummaryView> STUDENT_CHAT_LIST_ORDER = (a, b) -> {
        boolean aHasMessage = a.lastMessageAt() != null;
        boolean bHasMessage = b.lastMessageAt() != null;
        if (aHasMessage != bHasMessage) {
            return aHasMessage ? -1 : 1;
        }
        if (aHasMessage) {
            return b.lastMessageAt().compareTo(a.lastMessageAt());
        }
        return a.instructorName().compareTo(b.instructorName());
    };

    private final CoffeeChatRepository coffeeChatRepository;
    private final CoffeeChatMessageRepository coffeeChatMessageRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final InstructorPort instructorPort;

    @Override
    public List<CoffeeChatSummaryView> getStudentChats(Long studentId) {
        List<CoffeeChat> chats = coffeeChatRepository.findAllByStudentId(studentId);
        return toSummaryViews(chats, studentId).stream()
                .sorted(STUDENT_CHAT_LIST_ORDER)
                .collect(Collectors.toList());
    }

    @Override
    public List<CoffeeChatSummaryView> getInstructorPendingChats(Long instructorId) {
        List<CoffeeChat> chats = coffeeChatRepository
                .findAllByInstructorIdAndStatusOrderByCreatedAtAsc(instructorId, CoffeeChatStatus.PENDING);
        List<Long> chatIds = chats.stream().map(CoffeeChat::getId).collect(Collectors.toList());
        Map<Long, CoffeeChatMessage> latestMessages =
                coffeeChatMessageRepository.findLatestMessagesByCoffeeChatIds(chatIds);

        List<CoffeeChat> chatsWithMessages = chats.stream()
                .filter(chat -> latestMessages.containsKey(chat.getId()))
                .collect(Collectors.toList());

        return toSummaryViews(chatsWithMessages, instructorId);
    }

    @Override
    public List<CoffeeChatSummaryView> getInstructorActiveChats(Long instructorId) {
        List<CoffeeChat> chats = coffeeChatRepository
                .findAllByInstructorIdAndStatusOrderByAcceptedAtDesc(instructorId, CoffeeChatStatus.ACCEPTED);
        return toSummaryViews(chats, instructorId);
    }

    @Override
    public List<CoffeeChatMessage> getMessages(Long chatId, Long requesterId, int page, int size) {
        if (page < 0 || size < 1 || size > MAX_MESSAGE_PAGE_SIZE) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        CoffeeChat chat = coffeeChatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEE_CHAT_NOT_FOUND));

        if (!chat.isParticipant(requesterId)) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_FORBIDDEN);
        }

        return coffeeChatMessageRepository.findAllByCoffeeChatId(chatId, page, size);
    }

    private List<CoffeeChatSummaryView> toSummaryViews(List<CoffeeChat> chats, Long excludeSenderId) {
        List<Long> chatIds = chats.stream().map(CoffeeChat::getId).collect(Collectors.toList());
        Map<Long, Long> unreadCounts =
                coffeeChatMessageRepository.countUnreadMessagesByCoffeeChatIds(chatIds, excludeSenderId);
        Map<Long, CoffeeChatMessage> latestMessages =
                coffeeChatMessageRepository.findLatestMessagesByCoffeeChatIds(chatIds);

        List<Long> courseIds = chats.stream().map(CoffeeChat::getCourseId).distinct().collect(Collectors.toList());
        List<Long> userIds = chats.stream()
                .flatMap(chat -> Stream.of(chat.getInstructorId(), chat.getStudentId()))
                .distinct()
                .collect(Collectors.toList());
        List<Long> instructorIds = chats.stream().map(CoffeeChat::getInstructorId).distinct().collect(Collectors.toList());

        Map<Long, Course> coursesById = courseRepository.findAllByIdIn(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, course -> course));
        Map<Long, User> usersById = userRepository.findAllByIdIn(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        Map<Long, InstructorPort.InstructorInfo> instructorInfoById =
                instructorPort.getInstructorInfoBatch(instructorIds);

        return chats.stream()
                .map(chat -> {
                    Course course = coursesById.get(chat.getCourseId());
                    if (course == null) {
                        throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
                    }
                    User instructor = usersById.get(chat.getInstructorId());
                    User student = usersById.get(chat.getStudentId());
                    if (instructor == null || student == null) {
                        throw new BusinessException(ErrorCode.USER_NOT_FOUND);
                    }
                    String instructorProfileImagePath = instructorInfoById.get(chat.getInstructorId()).profileImagePath();
                    CoffeeChatMessage lastMessage = latestMessages.get(chat.getId());

                    return new CoffeeChatSummaryView(
                            chat.getId(),
                            chat.getStudentId(),
                            student.getLoginId(),
                            chat.getInstructorId(),
                            instructor.getName(),
                            chat.getCourseId(),
                            course.getTitle(),
                            chat.getStatus(),
                            chat.getCreatedAt(),
                            chat.getAcceptedAt(),
                            unreadCounts.getOrDefault(chat.getId(), 0L),
                            instructorProfileImagePath,
                            lastMessage != null ? lastMessage.getContent() : null,
                            lastMessage != null ? lastMessage.getCreatedAt() : null
                    );
                })
                .collect(Collectors.toList());
    }
}
