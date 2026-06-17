package com.sashimi.order.application.policy;

import com.sashimi.cart.application.port.CourseInfo;
import com.sashimi.cart.application.port.CoursePort;
import com.sashimi.enrollment.application.port.EnrollmentPort;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoursePurchasePolicy {

    private final CoursePort coursePort;
    private final EnrollmentPort enrollmentPort;

    public CourseInfo validatePurchasable(Long userId, Long courseId) {
        CourseInfo courseInfo = coursePort.getCourseInfo(courseId);

        if (!courseInfo.purchasable()) {
            throw new BusinessException(ErrorCode.COURSE_NOT_PURCHASABLE);
        }

        if (enrollmentPort.isEnrolled(userId, courseId)) {
            throw new BusinessException(ErrorCode.ENROLLMENT_ALREADY_EXISTS);
        }

        return courseInfo;
    }
}