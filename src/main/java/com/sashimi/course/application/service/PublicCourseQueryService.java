package com.sashimi.course.application.service;

import com.sashimi.course.application.port.CategoryPort;
import com.sashimi.course.application.port.InstructorPort;
import com.sashimi.course.application.query.PublicCourseDetailView;
import com.sashimi.course.application.query.PublicCourseView;
import com.sashimi.course.application.usecase.PublicCourseQueryUseCase;
import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseStatus;
import com.sashimi.course.domain.repository.CourseRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PublicCourseQueryService implements PublicCourseQueryUseCase {

    private final CourseRepository courseRepository;
    private final CategoryPort categoryPort;
    private final InstructorPort instructorPort;

    public PublicCourseQueryService(CourseRepository courseRepository,
                                    CategoryPort categoryPort,
                                    InstructorPort instructorPort) {
        this.courseRepository = courseRepository;
        this.categoryPort = categoryPort;
        this.instructorPort = instructorPort;
    }

    @Override
    public List<PublicCourseView> getAllApprovedCourses() {
        return courseRepository.findByStatus(CourseStatus.APPROVED)
                .stream()
                .map(this::toView)
                .toList();
    }

    @Override
    public List<PublicCourseView> getCoursesByCategory(String categoryName) {
        List<Long> categoryIds = categoryPort.getCategoryIdsByName(categoryName);
        return courseRepository.findByStatusAndCategoryIdIn(CourseStatus.APPROVED, categoryIds)
                .stream()
                .map(this::toView)
                .toList();
    }

    @Override
    public List<PublicCourseView> getCoursesBySubCategory(Long categoryId) {
        return courseRepository.findByStatusAndCategoryId(CourseStatus.APPROVED, categoryId)
                .stream()
                .map(this::toView)
                .toList();
    }

    @Override
    public PublicCourseDetailView getCourseDetail(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        if (course.getStatus() != CourseStatus.APPROVED) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }
        String instructorName = instructorPort.getInstructorName(course.getInstructorId());
        String categoryName = categoryPort.getCategoryNameById(course.getCategoryId());

        List<PublicCourseDetailView.SessionView> sessions = course.getSessions().stream()
                .map(s -> new PublicCourseDetailView.SessionView(
                        s.getId(),
                        s.getTitle(),
                        s.isPreview() ? s.getVideoUrl() : null,
                        s.getDurationSeconds(),
                        s.getSessionOrder(),
                        s.isPreview()
                ))
                .toList();

        return new PublicCourseDetailView(
                course.getId(), course.getTitle(), course.getDescription(),
                course.getPrice(), course.getDifficulty(), course.getThumbnail(),
                course.getTotalDuration(), course.getRatingAvg(), course.getReviewCount(),
                course.getStudentCount(), instructorName, categoryName, sessions
        );
    }

    private PublicCourseView toView(Course course) {
        String instructorName = instructorPort.getInstructorName(course.getInstructorId());
        return new PublicCourseView(
                course.getId(),
                instructorName,
                course.getTitle(),
                course.getPrice(),
                course.getThumbnail(),
                course.getTotalDuration(),
                course.getRatingAvg(),
                course.getStudentCount()
        );
    }
}