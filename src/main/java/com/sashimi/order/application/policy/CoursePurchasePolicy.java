package com.sashimi.order.application.policy;

import com.sashimi.cart.application.port.CourseInfo;
import com.sashimi.cart.application.port.CoursePort;
import com.sashimi.enrollment.application.port.EnrollmentPort;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CoursePurchasePolicy {

    private final CoursePort coursePort;
    private final EnrollmentPort enrollmentPort;

    public CourseInfo validatePurchasable(
            Long userId,
            Long courseId
    ) {
        CourseInfo courseInfo =
                coursePort.getCourseInfo(courseId);

        validateCourseStatus(courseInfo);

        if (enrollmentPort.isEnrolled(
                userId,
                courseId
        )) {
            throw new BusinessException(
                    ErrorCode.ENROLLMENT_ALREADY_EXISTS
            );
        }

        return courseInfo;
    }

    public List<CourseInfo> validatePurchasableCourses(
            Long userId,
            List<Long> courseIds
    ) {
        if (courseIds == null || courseIds.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.CART_EMPTY_SELECTION
            );
        }

        List<Long> distinctCourseIds = courseIds.stream()
                .distinct()
                .toList();

        Map<Long, CourseInfo> courseInfoMap =
                coursePort.getCourseInfos(
                        distinctCourseIds
                );

        Set<Long> enrolledCourseIds =
                enrollmentPort.findEnrolledCourseIds(
                        userId,
                        distinctCourseIds
                );

        return distinctCourseIds.stream()
                .map(courseId ->
                        validateCourse(
                                courseId,
                                courseInfoMap,
                                enrolledCourseIds
                        )
                )
                .toList();
    }

    private CourseInfo validateCourse(
            Long courseId,
            Map<Long, CourseInfo> courseInfoMap,
            Set<Long> enrolledCourseIds
    ) {
        CourseInfo courseInfo =
                courseInfoMap.get(courseId);

        if (courseInfo == null) {
            throw new BusinessException(
                    ErrorCode.COURSE_NOT_FOUND
            );
        }

        validateCourseStatus(courseInfo);

        if (enrolledCourseIds.contains(courseId)) {
            throw new BusinessException(
                    ErrorCode.ENROLLMENT_ALREADY_EXISTS
            );
        }

        return courseInfo;
    }

    private void validateCourseStatus(
            CourseInfo courseInfo
    ) {
        if (!courseInfo.purchasable()) {
            throw new BusinessException(
                    ErrorCode.COURSE_NOT_PURCHASABLE
            );
        }
    }
}