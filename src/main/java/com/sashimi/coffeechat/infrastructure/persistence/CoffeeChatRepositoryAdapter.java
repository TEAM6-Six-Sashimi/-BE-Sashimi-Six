package com.sashimi.coffeechat.infrastructure.persistence;

import com.sashimi.coffeechat.domain.model.CoffeeChat;
import com.sashimi.coffeechat.domain.model.CoffeeChatStatus;
import com.sashimi.coffeechat.domain.repository.CoffeeChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CoffeeChatRepositoryAdapter implements CoffeeChatRepository {

    private final SpringDataCoffeeChatRepository springDataRepository;

    @Override
    public CoffeeChat save(CoffeeChat coffeeChat) {
        CoffeeChatJpaEntity entity = CoffeeChatJpaEntity.from(coffeeChat);
        return springDataRepository.save(entity).toDomain();
    }

    @Override
    public Optional<CoffeeChat> findById(Long id) {
        return springDataRepository.findById(id)
                .map(CoffeeChatJpaEntity::toDomain);
    }

    @Override
    public List<CoffeeChat> findAllByStudentId(Long studentId) {
        return springDataRepository.findAllByStudentIdOrderByCreatedAtDesc(studentId)
                .stream()
                .map(CoffeeChatJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CoffeeChat> findAllByInstructorIdAndStatusOrderByCreatedAtAsc(Long instructorId, CoffeeChatStatus status) {
        return springDataRepository.findAllByInstructorIdAndStatusOrderByCreatedAtAsc(instructorId, status)
                .stream()
                .map(CoffeeChatJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CoffeeChat> findAllByInstructorIdAndStatusOrderByAcceptedAtDesc(Long instructorId, CoffeeChatStatus status) {
        return springDataRepository.findAllByInstructorIdAndStatusOrderByAcceptedAtDesc(instructorId, status)
                .stream()
                .map(CoffeeChatJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByStudentIdAndInstructorIdAndCourseId(Long studentId, Long instructorId, Long courseId) {
        return springDataRepository.existsByStudentIdAndInstructorIdAndCourseId(studentId, instructorId, courseId);
    }
}
