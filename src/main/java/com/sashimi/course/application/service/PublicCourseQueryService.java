package com.sashimi.course.application.service;

import com.sashimi.course.application.port.CategoryPort;
import com.sashimi.course.application.port.CourseReviewPort;
import com.sashimi.course.application.port.InstructorPort;
import com.sashimi.course.application.port.NcsInfoQueryPort;
import com.sashimi.course.application.port.NcsInfoView;
import com.sashimi.course.application.query.PublicCourseDetailView;
import com.sashimi.course.application.query.PublicCourseView;
import com.sashimi.course.application.usecase.PublicCourseQueryUseCase;
import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseStatus;
import com.sashimi.course.domain.repository.CourseRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.global.storage.FileStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PublicCourseQueryService implements PublicCourseQueryUseCase {

    /** 미리보기 영상 presigned URL 만료 (분) */
    private static final int PREVIEW_VIDEO_URL_EXPIRY_MINUTES = 120;

    private final CourseRepository courseRepository;
    private final CategoryPort categoryPort;
    private final InstructorPort instructorPort;
    private final NcsInfoQueryPort ncsInfoQueryPort;
    private final FileStoragePort fileStoragePort;
    private final CourseReviewPort courseReviewPort;

    @Override
    public List<PublicCourseView> getAllApprovedCourses() {
        List<Course> courses = courseRepository.findByStatus(CourseStatus.APPROVED);
        Set<Long> popularIds = resolvePopularIds(courses);
        return courses.stream().map(c -> toView(c, popularIds)).toList();
    }

    @Override
    public List<PublicCourseView> getCoursesByCategory(String categoryName) {
        List<Long> categoryIds = categoryPort.getCategoryIdsByName(categoryName);
        List<Course> courses = courseRepository.findByStatusAndCategoryIdIn(CourseStatus.APPROVED, categoryIds);
        Set<Long> popularIds = resolvePopularIds(courses);
        return courses.stream().map(c -> toView(c, popularIds)).toList();
    }

    @Override
    public List<PublicCourseView> getCoursesBySubCategory(Long categoryId) {
        List<Course> courses = courseRepository.findByStatusAndCategoryId(CourseStatus.APPROVED, categoryId);
        Set<Long> popularIds = resolvePopularIds(courses);
        return courses.stream().map(c -> toView(c, popularIds)).toList();
    }

    @Override
    public PublicCourseDetailView getCourseDetail(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        if (course.getStatus() != CourseStatus.APPROVED) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }
        InstructorPort.InstructorInfo instructorInfo = instructorPort.getInstructorInfo(course.getInstructorId());
        String mainCategoryName = categoryPort.getMainCategoryNameById(course.getCategoryId());
        String categoryName = categoryPort.getCategoryNameById(course.getCategoryId());

        Long ncsInfoId = categoryPort.getNcsInfoIdByCategoryId(course.getCategoryId());
        NcsInfoView ncs = ncsInfoId == null ? null : ncsInfoQueryPort.findViewByRepresentativeId(ncsInfoId).orElse(null);

        List<PublicCourseDetailView.SessionView> sessions = course.getSessions().stream()
                .map(s -> new PublicCourseDetailView.SessionView(
                        s.getId(),
                        s.getTitle(),
                        s.isPreview() ? resolveVideoUrl(s.getVideoUrl()) : null,
                        s.getDurationSeconds(),
                        s.getSessionOrder(),
                        s.isPreview()
                ))
                .toList();

        List<PublicCourseDetailView.ReviewView> reviews = courseReviewPort.findActiveReviewsByCourseId(courseId)
                .stream()
                .map(r -> new PublicCourseDetailView.ReviewView(
                        r.reviewId(), r.rating(), r.content(), r.writerLoginId(), r.createdAt()
                ))
                .toList();

        return new PublicCourseDetailView(
                course.getId(), course.getTitle(), course.getDescription(),
                course.getPrice(), course.getDifficulty(), course.getThumbnail(),
                course.getTotalDuration(), course.getRatingAvg(), course.getReviewCount(),
                course.getStudentCount(),
                new PublicCourseDetailView.InstructorView(
                        instructorInfo.name(), instructorInfo.profileImagePath(),
                        instructorInfo.bio(), instructorInfo.mainCareers(), instructorInfo.portfolioUrl()
                ),
                mainCategoryName, categoryName, ncs, course.getApprovedAt(), sessions, reviews
        );
    }

    private String resolveVideoUrl(String key) {
        if (key == null || key.isBlank() || key.startsWith("http")) {
            return key;
        }
        return fileStoragePort.generateVideoUrl(key, PREVIEW_VIDEO_URL_EXPIRY_MINUTES);
    }

    private Set<Long> resolvePopularIds(List<Course> courses) {
        return courses.stream()
                .collect(Collectors.groupingBy(Course::getCategoryId))
                .values().stream()
                .flatMap(group -> group.stream()
                        .sorted(Comparator.comparingInt(Course::getStudentCount).reversed())
                        .limit(3))
                .map(Course::getId)
                .collect(Collectors.toSet());
    }

    private String resolveLabel(Course course, Set<Long> popularIds) {
        if (popularIds.contains(course.getId())) return "POPULAR";
        if (course.getApprovedAt() != null &&
                course.getApprovedAt().isAfter(LocalDateTime.now().minusDays(30))) return "NEW";
        return null;
    }

    private PublicCourseView toView(Course course, Set<Long> popularIds) {
        String instructorName = instructorPort.getInstructorName(course.getInstructorId());
        return new PublicCourseView(
                course.getId(),
                instructorName,
                course.getTitle(),
                course.getPrice(),
                course.getThumbnail(),
                course.getTotalDuration(),
                course.getRatingAvg(),
                course.getStudentCount(),
                course.getApprovedAt(),
                resolveLabel(course, popularIds)
        );
    }
}