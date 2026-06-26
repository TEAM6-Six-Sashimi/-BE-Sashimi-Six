package com.sashimi.payment.application.service.checkout;

public record CheckoutCourse(
        Long courseId,
        String title,
        Long price
) {
}