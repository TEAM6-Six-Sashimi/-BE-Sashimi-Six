package com.sashimi.credit.presentation.api.response;

import com.sashimi.credit.application.usecase.CreditQueryUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CreditChargeHistoryResponse(

        @Schema(description = "크레딧 충전 내역")
        List<CreditChargeHistoryItemResponse> items,

        @Schema(description = "전체 충전 내역 수")
        long totalElements,

        @Schema(description = "전체 페이지 수")
        int totalPages,

        @Schema(description = "현재 페이지 번호")
        int page,

        @Schema(description = "페이지 크기")
        int size
) {
    public static CreditChargeHistoryResponse from(
            CreditQueryUseCase.CreditChargeHistory result
    ) {
        return new CreditChargeHistoryResponse(
                result.items()
                        .stream()
                        .map(CreditChargeHistoryItemResponse::from)
                        .toList(),
                result.totalElements(),
                result.totalPages(),
                result.page(),
                result.size()
        );
    }
}