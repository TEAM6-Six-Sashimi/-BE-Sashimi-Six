package com.sashimi.cart.application.port;

public record CourseInfo(

        Long courseId,
        String title,
        Long price,
        String thumbnail,
        String instructorName,
        boolean purchasable

) {
}
