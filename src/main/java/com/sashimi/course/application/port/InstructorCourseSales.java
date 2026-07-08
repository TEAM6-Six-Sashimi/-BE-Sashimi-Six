package com.sashimi.course.application.port;

public record InstructorCourseSales (
    Long courseId,
    String courseTitle,
    Long salesAmount
) {
    public InstructorCourseSales {
            if (salesAmount == null) {
                salesAmount = 0L;
            }
        }
    }