package com.sashimi.course.application.service;

import com.sashimi.course.application.port.CategoryPort;
import com.sashimi.course.application.port.CourseReviewPort;
import com.sashimi.course.application.port.EnrollmentQueryPort;
import com.sashimi.course.application.port.InstructorPort;
import com.sashimi.course.application.port.NcsInfoQueryPort;
import com.sashimi.course.application.port.NcsInfoView;
import com.sashimi.course.application.query.CourseViewerType;
import com.sashimi.course.application.query.PublicCourseDetailView;
import com.sashimi.course.application.query.PublicCourseView;
import com.sashimi.course.application.query.RejectReasonView;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PublicCourseQueryService implements PublicCourseQueryUseCase {

    /** 영상 시청 presigned URL 만료 (분) */
    private static final int VIDEO_URL_EXPIRY_MINUTES = 120;
    /** 자료 다운로드 presigned URL 만료 (분) */
    private static final int ATTACHMENT_URL_EXPIRY_MINUTES = 120;

    private final CourseRepository courseRepository;
    private final CategoryPort categoryPort;
    private final InstructorPort instructorPort;
    private final NcsInfoQueryPort ncsInfoQueryPort;
    private final FileStoragePort fileStoragePort;
    private final CourseReviewPort courseReviewPort;
    private final EnrollmentQueryPort enrollmentQueryPort;

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
    public List<PublicCourseView> getCoursesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<Course> courses = courseRepository.findByStatusAndIdIn(CourseStatus.APPROVED, ids);
        Set<Long> popularIds = resolvePopularIds(courses);
        Map<Long, Course> byId = courses.stream().collect(Collectors.toMap(Course::getId, c -> c));
        // 요청한 id 순서(추천 랭킹)를 유지하고, 없는 id는 건너뜀
        return ids.stream()
                .distinct()
                .map(byId::get)
                .filter(java.util.Objects::nonNull)
                .map(c -> toView(c, popularIds))
                .toList();
    }

    @Override
    public RejectReasonView getRejectReason(Long courseId, Long userId, boolean isAdmin) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        // 본인 강의 강사 또는 관리자만 조회 가능
        boolean isOwner = userId != null && course.getInstructorId().equals(userId);
        if (!isAdmin && !isOwner) {
            throw new BusinessException(ErrorCode.COURSE_FORBIDDEN);
        }
        return new RejectReasonView(
                course.getId(), course.getTitle(), course.getUpdatedAt(),
                course.getRejectReasonCategory(), course.getRejectDetail()
        );
    }

    @Override
    public PublicCourseDetailView getCourseDetail(Long courseId, Long userId, boolean isAdmin) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        boolean isOwner = userId != null && course.getInstructorId().equals(userId);

        // 관리자·본인 강의가 아닌 경우 수강 여부 확인 (만료 시 미수강 처리)
        EnrollmentQueryPort.EnrollmentProgress progress = null;
        if (userId != null && !isAdmin && !isOwner) {
            progress = enrollmentQueryPort.findActiveEnrollment(userId, courseId).orElse(null);
        }
        boolean isEnrolled = progress != null;

        // 관리자·본인 강사는 모든 상태 조회 가능
        boolean canSeeAnyStatus = isAdmin || isOwner;
        // 수강생은 폐강(CLOSED)된 강의도 수강 기간 동안 조회 가능 (APPROVED + CLOSED)
        boolean enrolledVisible = isEnrolled
                && (course.getStatus() == CourseStatus.APPROVED || course.getStatus() == CourseStatus.CLOSED);
        // 그 외(비로그인·미수강)는 APPROVED만
        boolean publicVisible = course.getStatus() == CourseStatus.APPROVED;
        if (!canSeeAnyStatus && !enrolledVisible && !publicVisible) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }

        CourseViewerType viewerType = resolveViewerType(isAdmin, isOwner, isEnrolled);
        // 전체 영상·자료 접근 가능 여부
        boolean fullAccess = isAdmin || isOwner || isEnrolled;

        InstructorPort.InstructorInfo instructorInfo = instructorPort.getInstructorInfo(course.getInstructorId());
        String mainCategoryName = categoryPort.getMainCategoryNameById(course.getCategoryId());
        String categoryName = categoryPort.getCategoryNameById(course.getCategoryId());

        Long ncsInfoId = categoryPort.getNcsInfoIdByCategoryId(course.getCategoryId());
        NcsInfoView ncs = ncsInfoId == null ? null : ncsInfoQueryPort.findViewByRepresentativeId(ncsInfoId).orElse(null);

        // 수강생만 세션별 진행 정보(이어보기·세션 진행률) 조회
        Map<Long, EnrollmentQueryPort.SessionProgress> sessionProgressMap = isEnrolled
                ? enrollmentQueryPort.findSessionProgresses(userId, courseId).stream()
                        .collect(Collectors.toMap(EnrollmentQueryPort.SessionProgress::sessionId, p -> p))
                : Map.of();

        List<PublicCourseDetailView.SessionView> sessions = course.getSessions().stream()
                .map(s -> {
                    boolean canWatch = fullAccess || s.isPreview();
                    String videoUrl = canWatch ? resolveVideoUrl(s.getVideoUrl()) : null;
                    EnrollmentQueryPort.SessionProgress sp = sessionProgressMap.get(s.getId());
                    return new PublicCourseDetailView.SessionView(
                            s.getId(),
                            s.getSessionUid(),
                            s.getTitle(),
                            videoUrl,
                            s.getDurationSeconds(),
                            s.getSessionOrder(),
                            s.isPreview(),
                            fullAccess ? s.getAttachmentName() : null,
                            fullAccess ? resolveAttachmentUrl(s.getAttachmentUrl()) : null,
                            fullAccess ? s.getAttachmentType() : null,
                            fullAccess ? s.getAttachmentSize() : null,
                            isEnrolled ? (sp != null ? sp.lastPositionSeconds() : 0) : null,
                            isEnrolled ? (sp != null ? sp.progressRate() : BigDecimal.ZERO) : null,
                            isEnrolled ? (sp != null && sp.completed()) : null
                    );
                })
                .toList();

        List<PublicCourseDetailView.ReviewView> reviews = courseReviewPort.findActiveReviewsByCourseId(courseId)
                .stream()
                .map(r -> new PublicCourseDetailView.ReviewView(
                        r.reviewId(), r.rating(), r.content(), r.writerLoginId(), r.createdAt()
                ))
                .toList();

        // 별점 분포 집계 (5~1, 0개여도 항상 포함)
        Map<Integer, Long> ratingCounts = reviews.stream()
                .collect(Collectors.groupingBy(PublicCourseDetailView.ReviewView::rating, Collectors.counting()));
        List<PublicCourseDetailView.RatingDistributionView> ratingDistribution = IntStream.rangeClosed(1, 5)
                .map(i -> 6 - i)
                .mapToObj(star -> new PublicCourseDetailView.RatingDistributionView(
                        star, ratingCounts.getOrDefault(star, 0L).intValue()))
                .toList();

        CourseStatus status = course.getStatus();
        String rejectReason = course.getRejectReason();
        BigDecimal progressRate = isEnrolled ? progress.progressRate() : null;
        Boolean completed = isEnrolled ? progress.completed() : null;

        return new PublicCourseDetailView(
                viewerType,
                course.getId(), course.getTitle(), course.getDescription(),
                course.getPrice(), course.getDifficulty(), course.getThumbnail(),
                course.getTotalDuration(), course.getRatingAvg(), course.getReviewCount(),
                course.getStudentCount(),
                new PublicCourseDetailView.InstructorView(
                        instructorInfo.name(), instructorInfo.profileImagePath(),
                        instructorInfo.bio(), instructorInfo.mainCareers(), instructorInfo.portfolioUrl()
                ),
                mainCategoryName, categoryName, ncs, course.getApprovedAt(),
                status, rejectReason, progressRate, completed, sessions, reviews, ratingDistribution
        );
    }

    private CourseViewerType resolveViewerType(boolean isAdmin, boolean isOwner, boolean isEnrolled) {
        if (isAdmin) return CourseViewerType.ADMIN;
        if (isOwner) return CourseViewerType.OWNER;
        if (isEnrolled) return CourseViewerType.ENROLLED;
        return CourseViewerType.PUBLIC;
    }

    private String resolveVideoUrl(String key) {
        if (key == null || key.isBlank() || key.startsWith("http")) {
            return key;
        }
        return fileStoragePort.generateVideoUrl(key, VIDEO_URL_EXPIRY_MINUTES);
    }

    private String resolveAttachmentUrl(String key) {
        if (key == null || key.isBlank() || key.startsWith("http")) {
            return key;
        }
        return fileStoragePort.generateAttachmentUrl(key, ATTACHMENT_URL_EXPIRY_MINUTES);
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