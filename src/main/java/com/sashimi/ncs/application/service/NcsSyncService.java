package com.sashimi.ncs.application.service;

import com.sashimi.category.infrastructure.persistence.CategoryJpaEntity;
import com.sashimi.category.infrastructure.persistence.SpringDataCategoryRepository;
import com.sashimi.ncs.infrastructure.persistence.NcsCategoryMappingJpaEntity;
import com.sashimi.ncs.infrastructure.persistence.NcsInfoJpaEntity;
import com.sashimi.ncs.infrastructure.persistence.SpringDataNcsCategoryMappingRepository;
import com.sashimi.ncs.infrastructure.persistence.SpringDataNcsInfoRepository;
import com.sashimi.ncs.infrastructure.publicdata.NcsApiClient;
import com.sashimi.ncs.infrastructure.publicdata.NcsCompeUnitApiResponse;
import com.sashimi.ncs.infrastructure.publicdata.NcsDutyApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NcsSyncService {

    private static final String SUCCESS_CODE = "000";

    private final NcsApiClient ncsApiClient;
    private final SpringDataNcsInfoRepository ncsInfoRepository;
    private final SpringDataNcsCategoryMappingRepository mappingRepository;
    private final SpringDataCategoryRepository categoryRepository;

    public int syncByDutyCd(String dutyCd) {
        NcsDutyApiResponse dutyResponse = ncsApiClient.fetchNcsDutyInfoAsDto(dutyCd, 1);
        validateDutyResponse(dutyResponse, dutyCd);

        NcsDutyApiResponse.NcsDutyItem duty = dutyResponse.data().get(0);
        List<NcsCompeUnitApiResponse.NcsCompeUnitItem> units = fetchAllCompeUnits(dutyCd);

        LocalDateTime now = LocalDateTime.now();
        int syncedCount = 0;

        for (NcsCompeUnitApiResponse.NcsCompeUnitItem unit : units) {
            upsertNcsInfo(duty, unit, now);
            syncedCount++;
        }

        mapCategoriesByDutyCd(dutyCd);

        return syncedCount;
    }

    public void syncDefaultMappingsIfNeeded() {
        List<NcsCategoryMappingJpaEntity> mappings = mappingRepository.findByActiveTrue();

        for (NcsCategoryMappingJpaEntity mapping : mappings) {
            syncMappingIfNeeded(mapping);
        }
    }

    private void syncMappingIfNeeded(NcsCategoryMappingJpaEntity mapping) {
        if (!ncsInfoRepository.existsByJobName(mapping.getNcsJobName())) {
            syncByDutyCd(mapping.getDutyCd());
        }

        connectCategoryToNcsInfo(mapping.getCategoryName(), mapping.getNcsJobName());
    }

    private void mapCategoriesByDutyCd(String dutyCd) {
        List<NcsCategoryMappingJpaEntity> mappings = mappingRepository.findByDutyCdAndActiveTrue(dutyCd);

        for (NcsCategoryMappingJpaEntity mapping : mappings) {
            connectCategoryToNcsInfo(mapping.getCategoryName(), mapping.getNcsJobName());
        }
    }

    private void connectCategoryToNcsInfo(String categoryName, String ncsJobName) {
        CategoryJpaEntity category = categoryRepository.findBySubCategoryAndActiveTrue(categoryName)
                .orElse(null);

        if (category == null) {
            return;
        }

        NcsInfoJpaEntity ncsInfo = ncsInfoRepository.findFirstByJobNameOrderByIdAsc(ncsJobName)
                .orElse(null);

        if (ncsInfo == null) {
            return;
        }

        category.updateNcsInfoId(ncsInfo.getId());
    }

    private List<NcsCompeUnitApiResponse.NcsCompeUnitItem> fetchAllCompeUnits(String dutyCd) {
        List<NcsCompeUnitApiResponse.NcsCompeUnitItem> result = new ArrayList<>();

        NcsCompeUnitApiResponse firstResponse = ncsApiClient.fetchNcsCompeUnitInfoAsDto(dutyCd, 1);
        validateCompeUnitResponse(firstResponse, dutyCd);

        if (firstResponse.data() != null) {
            result.addAll(firstResponse.data());
        }

        int totalPage = firstResponse.dataInfo() == null ? 1 : firstResponse.dataInfo().totalPage();

        for (int pageNo = 2; pageNo <= totalPage; pageNo++) {
            NcsCompeUnitApiResponse response = ncsApiClient.fetchNcsCompeUnitInfoAsDto(dutyCd, pageNo);
            validateCompeUnitResponse(response, dutyCd);

            if (response.data() != null) {
                result.addAll(response.data());
            }
        }

        return result;
    }

    private void upsertNcsInfo(NcsDutyApiResponse.NcsDutyItem duty,
                               NcsCompeUnitApiResponse.NcsCompeUnitItem unit,
                               LocalDateTime syncedAt) {
        ncsInfoRepository.findByNcsCode(unit.ncsClCd())
                .ifPresentOrElse(
                        ncsInfo -> ncsInfo.updateFromSync(
                                duty.dutyNm(),
                                duty.dutyNm(),
                                duty.dutyDef(),
                                unit.compUnitCd(),
                                cleanAbilityUnitName(unit.compUnitName()),
                                unit.compUnitDef(),
                                syncedAt
                        ),
                        () -> ncsInfoRepository.save(new NcsInfoJpaEntity(
                                unit.ncsClCd(),
                                duty.dutyNm(),
                                duty.dutyNm(),
                                duty.dutyDef(),
                                unit.compUnitCd(),
                                cleanAbilityUnitName(unit.compUnitName()),
                                unit.compUnitDef(),
                                syncedAt
                        ))
                );
    }

    private void validateDutyResponse(NcsDutyApiResponse response, String dutyCd) {
        if (response == null || response.dataInfo() == null) {
            throw new IllegalStateException("NCS 직무정보 응답이 비어 있습니다. dutyCd=" + dutyCd);
        }

        if (!SUCCESS_CODE.equals(response.dataInfo().code())) {
            throw new IllegalStateException("NCS 직무정보 조회 실패. dutyCd=" + dutyCd
                    + ", message=" + response.dataInfo().message());
        }

        if (response.data() == null || response.data().isEmpty()) {
            throw new IllegalStateException("NCS 직무정보가 없습니다. dutyCd=" + dutyCd);
        }
    }

    private void validateCompeUnitResponse(NcsCompeUnitApiResponse response, String dutyCd) {
        if (response == null || response.dataInfo() == null) {
            throw new IllegalStateException("NCS 능력단위 응답이 비어 있습니다. dutyCd=" + dutyCd);
        }

        if (!SUCCESS_CODE.equals(response.dataInfo().code())) {
            throw new IllegalStateException("NCS 능력단위 조회 실패. dutyCd=" + dutyCd
                    + ", message=" + response.dataInfo().message());
        }
    }

    private String cleanAbilityUnitName(String abilityUnitName) {
        if (abilityUnitName == null) {
            return null;
        }

        return abilityUnitName.replaceFirst("^\\d+\\.", "").trim();
    }
}