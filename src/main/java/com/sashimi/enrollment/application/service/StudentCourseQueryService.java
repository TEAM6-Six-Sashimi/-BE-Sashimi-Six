package com.sashimi.enrollment.application.service;

import com.sashimi.enrollment.application.port.CourseDetailInfo;
import com.sashimi.enrollment.application.port.CoursePort;
import com.sashimi.enrollment.application.port.EnrolledCourseInfo;
import com.sashimi.enrollment.application.port.EnrollmentPort;
import com.sashimi.enrollment.application.port.EnrollmentSummary;
import com.sashimi.enrollment.application.query.EnrolledCourseDetailView;
import com.sashimi.enrollment.application.query.EnrolledCourseView;
import com.sashimi.enrollment.application.usecase.StudentCourseQueryUseCase;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.global.storage.FileStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudentCourseQueryService implements StudentCourseQueryUseCase {

    /** 강의 구매일(수강 등록일) 기준 수강 가능 기간 (년) */
    private static final int ACCESS_PERIOD_YEARS = 2;

    /** 영상 시청 presigned URL 만료 (분) */
    private static final int VIDEO_URL_EXPIRY_MINUTES = 120;
    /** 자료 다운로드 presigned URL 만료 (분) */
    private static final int ATTACHMENT_URL_EXPIRY_MINUTES = 120;

    private final EnrollmentPort enrollmentPort;
    private final CoursePort coursePort;
    private final FileStoragePort fileStoragePort;

    @Override
    public List<EnrolledCourseView> getEnrolledCourses(Long userId) {
        return enrollmentPort.getEnrollmentsByUser(userId).stream()
                .filter(summary -> !isAccessExpired(summary.enrolledAt()))
                .map(summary -> {
                    EnrolledCourseInfo courseInfo = coursePort.getCourseInfo(summary.courseId());
                    return new EnrolledCourseView(
                            summary.courseId(),
                            courseInfo.title(),
                            courseInfo.thumbnail(),
                            courseInfo.instructorName(),
                            summary.progressRate(),
                            summary.completed(),
                            courseInfo.categoryId(),
                            courseInfo.categoryName(),
                            courseInfo.mainCategoryName()
                    );
                })
                .toList();
    }

    @Override
    public EnrolledCourseDetailView getEnrolledCourseDetail(Long userId, Long courseId) {
        EnrollmentSummary summary = enrollmentPort.getEnrollmentByCourse(userId, courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_FORBIDDEN));
        if (isAccessExpired(summary.enrolledAt())) {
            throw new BusinessException(ErrorCode.ENROLLMENT_EXPIRED);
        }
        return new EnrolledCourseDetailView(
                toPresignedCourse(coursePort.getCourseDetailInfo(courseId)),
                summary.progressRate(),
                summary.completed()
        );
    }

    /** 세션의 영상/자료 key를 시청·다운로드용 presigned URL로 변환 */
    private CourseDetailInfo toPresignedCourse(CourseDetailInfo info) {
        List<CourseDetailInfo.SessionInfo> sessions = info.sessions().stream()
                .map(s -> new CourseDetailInfo.SessionInfo(
                        s.sessionId(), s.sessionUid(), s.title(),
                        resolveVideoUrl(s.videoUrl()),
                        s.durationSeconds(), s.sessionOrder(), s.preview(),
                        s.attachmentName(),
                        resolveAttachmentUrl(s.attachmentUrl()),
                        s.attachmentType(), s.attachmentSize()
                ))
                .toList();

        return new CourseDetailInfo(
                info.courseId(), info.title(), info.description(), info.price(),
                info.difficulty(), info.thumbnail(), info.totalDuration(), info.ratingAvg(),
                info.reviewCount(), info.studentCount(), info.instructorName(),
                info.categoryName(), sessions
        );
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

    /** 구매일로부터 수강 가능 기간이 지났는지 여부 */
    private boolean isAccessExpired(LocalDateTime enrolledAt) {
        if (enrolledAt == null) {
            return false;
        }
        return enrolledAt.isBefore(LocalDateTime.now().minusYears(ACCESS_PERIOD_YEARS));
    }
}
