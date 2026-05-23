package com.sashimi.cart.application.port;

import java.math.BigDecimal;

public record CourseInfo(

        Long courseId,
        String title,
        BigDecimal price,
        String thumbnail,
        String instructorName,
        boolean purchasable

) {
}
