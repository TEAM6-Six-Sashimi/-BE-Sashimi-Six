package com.sashimi.coffeechat.domain.repository;

import com.sashimi.coffeechat.domain.model.CoffeeChat;
import com.sashimi.coffeechat.domain.model.CoffeeChatStatus;

import java.util.List;
import java.util.Optional;

public interface CoffeeChatRepository {

    CoffeeChat save(CoffeeChat coffeeChat);

    Optional<CoffeeChat> findById(Long id);

    List<CoffeeChat> findAllByStudentId(Long studentId);

    List<CoffeeChat> findAllByInstructorIdAndStatusOrderByCreatedAtAsc(Long instructorId, CoffeeChatStatus status);

    List<CoffeeChat> findAllByInstructorIdAndStatusOrderByAcceptedAtDesc(Long instructorId, CoffeeChatStatus status);

    boolean existsByStudentIdAndInstructorIdAndCourseIdAndStatusIn(
            Long studentId, Long instructorId, Long courseId, List<CoffeeChatStatus> statuses);

    void deleteById(Long id);
}
