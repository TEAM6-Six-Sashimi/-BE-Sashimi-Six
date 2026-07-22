package com.sashimi.course.infrastructure.persistence;

import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseSession;
import com.sashimi.course.domain.model.CourseStatus;
import com.sashimi.course.domain.repository.CourseRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class CourseRepositoryAdapter implements CourseRepository {

    private final SpringDataCourseRepository springDataCourseRepository;

    public CourseRepositoryAdapter(SpringDataCourseRepository springDataCourseRepository) {
        this.springDataCourseRepository = springDataCourseRepository;
    }

    @Override
    public Course save(Course course) {
        if (course.getId() == null) {
            return saveNew(course);
        }
        return saveExisting(course);
    }

    private Course saveNew(Course course) {
        CourseJpaEntity entity = new CourseJpaEntity(
                course.getInstructorId(), course.getCategoryId(), course.getTitle(),
                course.getDescription(), course.getPrice(), course.getDifficulty(),
                course.getThumbnail(), course.getTotalDuration(), course.getStatus(),
                course.getRejectReason(), course.getRatingAvg(), course.getReviewCount(),
                course.getStudentCount(), course.getCreatedAt(), course.getUpdatedAt(),
                course.getApprovedAt()
        );
        for (CourseSession session : course.getSessions()) {
            entity.addSession(toSessionEntity(session));
        }
        return toDomain(springDataCourseRepository.save(entity));
    }

    private Course saveExisting(Course course) {
        CourseJpaEntity entity = springDataCourseRepository.findById(course.getId())
                .orElseThrow(() -> new IllegalStateException("Course not found: " + course.getId()));

        entity.update(course.getCategoryId(), course.getTitle(), course.getDescription(),
                course.getPrice(), course.getDifficulty(), course.getThumbnail(),
                course.getTotalDuration(), course.getStatus(), course.getUpdatedAt());

        if (course.getStatus() == CourseStatus.APPROVED) {
            entity.approve(course.getApprovedAt(), course.getUpdatedAt());
        } else if (course.getStatus() == CourseStatus.REJECTED) {
            entity.reject(course.getRejectReason(), course.getRejectReasonCategory(),
                    course.getRejectDetail(), course.getUpdatedAt());
        }

        entity.markArchived(course.isArchived());

        entity.clearSessions();
        for (CourseSession session : course.getSessions()) {
            entity.addSession(toSessionEntity(session));
        }

        return toDomain(springDataCourseRepository.save(entity));
    }

    @Override
    public Optional<Course> findById(Long id) {
        return springDataCourseRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Course> findAllByIdIn(List<Long> ids) {
        // sessions는 LAZY라 toDomain()으로 건별 접근하면 N+1이 재발함 - 커피챗 목록조회처럼 sessions가 안 쓰이는 배치조회 전용
        return springDataCourseRepository.findAllById(ids)
                .stream().map(this::toDomainWithoutSessions).toList();
    }

    @Override
    public List<Course> findByInstructorIdAndStatus(Long instructorId, CourseStatus status) {
        return springDataCourseRepository.findByInstructorIdAndStatus(instructorId, status)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Course> findByInstructorIdAndStatusIn(Long instructorId, List<CourseStatus> statuses) {
        return springDataCourseRepository.findByInstructorIdAndStatusIn(instructorId, statuses)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Course> findByStatus(CourseStatus status) {
        return springDataCourseRepository.findByStatus(status)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Course> findByStatusIn(List<CourseStatus> statuses) {
        return springDataCourseRepository.findByStatusIn(statuses)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Course> findByStatusAndCategoryId(CourseStatus status, Long categoryId) {
        return springDataCourseRepository.findByStatusAndCategoryId(status, categoryId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Course> findByStatusAndCategoryIdIn(CourseStatus status, List<Long> categoryIds) {
        return springDataCourseRepository.findByStatusAndCategoryIdIn(status, categoryIds)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Course> findByStatusAndIdIn(CourseStatus status, List<Long> ids) {
        return springDataCourseRepository.findByStatusAndIdIn(status, ids)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Course> findByStatusAndApprovedAtBefore(CourseStatus status, LocalDateTime cutoff) {
        return springDataCourseRepository.findByStatusAndApprovedAtBefore(status, cutoff)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Course> findByStatusAndArchivedFalse(CourseStatus status) {
        return springDataCourseRepository.findByStatusAndArchivedFalse(status)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Course> searchApprovedByKeyword(
            String keyword,
            int limit
    ) {
        return springDataCourseRepository.searchApprovedByKeyword(
                        keyword,
                        PageRequest.of(0, limit)
                )
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        springDataCourseRepository.deleteById(id);
    }

    private Course toDomain(CourseJpaEntity entity) {
        List<CourseSession> sessions = entity.getSessions().stream()
                .map(s -> CourseSession.restore(s.getId(), s.getSessionUid(), entity.getId(),
                        s.getTitle(), s.getVideoUrl(), s.getDurationSeconds(), s.getSessionOrder(),
                        s.isPreview(), s.getAttachmentName(), s.getAttachmentUrl(),
                        s.getAttachmentType(), s.getAttachmentSize(), s.getCreatedAt(), s.getUpdatedAt()))
                .toList();
        return toDomain(entity, sessions);
    }

    private Course toDomainWithoutSessions(CourseJpaEntity entity) {
        return toDomain(entity, List.of());
    }

    private Course toDomain(CourseJpaEntity entity, List<CourseSession> sessions) {
        return Course.restore(entity.getId(), entity.getInstructorId(), entity.getCategoryId(),
                entity.getTitle(), entity.getDescription(), entity.getPrice(), entity.getDifficulty(),
                entity.getThumbnail(), entity.getTotalDuration(), entity.getStatus(),
                entity.getRejectReason(), entity.getRejectReasonCategory(), entity.getRejectDetail(),
                entity.getRatingAvg(), entity.getReviewCount(),
                entity.getStudentCount(), entity.getCreatedAt(), entity.getUpdatedAt(),
                entity.getApprovedAt(), entity.isArchived(), sessions);
    }

    private CourseSessionJpaEntity toSessionEntity(CourseSession session) {
        return new CourseSessionJpaEntity(session.getSessionUid(), session.getTitle(),
                session.getVideoUrl(), session.getDurationSeconds(), session.getSessionOrder(),
                session.isPreview(), session.getAttachmentName(), session.getAttachmentUrl(),
                session.getAttachmentType(), session.getAttachmentSize(),
                session.getCreatedAt(), session.getUpdatedAt());
    }
}
