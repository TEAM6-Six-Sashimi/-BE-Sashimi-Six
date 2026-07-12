package com.sashimi.coffeechat.application.usecase;

import com.sashimi.coffeechat.application.query.CoffeeChatSummaryView;
import com.sashimi.coffeechat.domain.model.CoffeeChatMessage;
import com.sashimi.course.domain.model.Course;

import java.util.List;

public interface CoffeeChatQueryUseCase {

    List<CoffeeChatSummaryView> getStudentChats(Long studentId);

    List<CoffeeChatSummaryView> getInstructorPendingChats(Long instructorId);

    List<CoffeeChatSummaryView> getInstructorActiveChats(Long instructorId);

    List<CoffeeChatMessage> getMessages(Long chatId, Long requesterId, int page, int size);

    List<Course> getApplicableCourses(Long studentId, Long instructorId);
}
