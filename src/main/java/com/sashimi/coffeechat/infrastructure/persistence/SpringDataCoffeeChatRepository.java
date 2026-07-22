package com.sashimi.coffeechat.infrastructure.persistence;

import com.sashimi.coffeechat.domain.model.CoffeeChatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataCoffeeChatRepository extends JpaRepository<CoffeeChatJpaEntity, Long> {

    List<CoffeeChatJpaEntity> findAllByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<CoffeeChatJpaEntity> findAllByInstructorIdAndStatusOrderByCreatedAtAsc(Long instructorId, CoffeeChatStatus status);

    List<CoffeeChatJpaEntity> findAllByInstructorIdAndStatusOrderByAcceptedAtDesc(Long instructorId, CoffeeChatStatus status);

    boolean existsByStudentIdAndInstructorIdAndCourseId(Long studentId, Long instructorId, Long courseId);
}
