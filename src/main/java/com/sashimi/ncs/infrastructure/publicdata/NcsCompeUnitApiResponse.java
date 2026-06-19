package com.sashimi.ncs.infrastructure.publicdata;

import java.util.List;

public record NcsCompeUnitApiResponse(
        List<NcsCompeUnitItem> data,
        DataInfo dataInfo
) {
    public record NcsCompeUnitItem(
            String dutyCd,
            String dutySvcNo,
            String ncsClCd,
            String compUnitCd,
            String compUnitName,
            String compUnitDef,
            Integer compUnitLevel
    ) {}

    public record DataInfo(
            String code,
            String message,
            int totalPage,
            String pageNo,
            int totCnt
    ) {}
}