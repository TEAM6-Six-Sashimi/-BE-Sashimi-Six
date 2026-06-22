package com.sashimi.subscription.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public enum SubscriptionPlan {

    MONTHLY(
            "1개월 플랜",
            1,
            10_000L,
            10_000L,
            0,
            List.of(
                    "AI 채용 공고 분석",
                    "AI 이력서 작성 및 평가",
                    "기업 내 무제한 이용"
            )
    ),

    ANNUAL(
            "12개월 플랜",
            12,
            120_000L,
            100_000L,
            17,
            List.of(
                    "AI 채용 공고 분석",
                    "AI 이력서 작성 및 평가",
                    "기간 내 무제한 이용",
                    "장기 이용 할인"
            )
    );

    private final String planName;
    private final int durationMonths;
    private final Long originalPrice;
    private final Long price;
    private final int discountRate;
    private final List<String> features;

    SubscriptionPlan(
            String planName,
            int durationMonths,
            Long originalPrice,
            Long price,
            int discountRate,
            List<String> features
    ) {
        this.planName = planName;
        this.durationMonths = durationMonths;
        this.originalPrice = originalPrice;
        this.price = price;
        this.discountRate = discountRate;
        this.features = List.copyOf(features);
    }

    public LocalDateTime calculateExpiration(
            LocalDateTime startedAt
    ) {
        return startedAt.plusMonths(durationMonths);
    }

    public String getPlanName() {
        return planName;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public Long getOriginalPrice() {
        return originalPrice;
    }

    public Long getPrice() {
        return price;
    }

    public int getDiscountRate() {
        return discountRate;
    }

    public List<String> getFeatures() {
        return features;
    }
}