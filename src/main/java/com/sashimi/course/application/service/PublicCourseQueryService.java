package com.sashimi.course.application.service;

import com.sashimi.course.application.port.CategoryPort;
import com.sashimi.course.application.port.InstructorPort;
import com.sashimi.course.application.query.PublicCourseView;
import com.sashimi.course.application.usecase.PublicCourseQueryUseCase;
import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseStatus;
import com.sashimi.course.domain.repository.CourseRepository;
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

    private PublicCourseView toView(Course course) {
        String instructorName = instructorPort.getInstructorName(course.getInstructorId());
        return new PublicCourseView(
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