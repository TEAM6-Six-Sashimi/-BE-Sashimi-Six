package com.sashimi.ncs.infrastructure.publicdata;

import java.util.List;

public record NcsDutyApiResponse(
        List<NcsDutyItem> data,
        DataInfo dataInfo
) {
    public record NcsDutyItem(
            String dutyCd,
            String dutyNm,
            String dutySvcNo,
            String dutyDef
    ) {}

    public record DataInfo(
            String code,
            String message,
            int totalPage,
            String pageNo,
            int totCnt
    ) {}
}