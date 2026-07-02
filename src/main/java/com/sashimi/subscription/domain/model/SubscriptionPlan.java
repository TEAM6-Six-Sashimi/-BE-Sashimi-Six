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
            "/files/images?key=images%2F531e6874-8223-419c-b78c-a17ad449b853.png",
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
            "/files/images?key=images%2Fd1b346c6-476f-4f44-86c1-0c9c8b94fed8.png",
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
    private final String planThumbnail;
    private final List<String> features;

    SubscriptionPlan(
            String planName,
            int durationMonths,
            Long originalPrice,
            Long price,
            int discountRate,
            String planThumbnail,
            List<String> features
    ) {
        this.planName = planName;
        this.durationMonths = durationMonths;
        this.originalPrice = originalPrice;
        this.price = price;
        this.discountRate = discountRate;
        this.planThumbnail = planThumbnail;
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

    public String getPlanThumbnail() {return planThumbnail;}
}